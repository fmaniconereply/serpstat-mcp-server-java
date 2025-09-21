// ==================== MAIN SERVER CLASS ====================
package com.serpstat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serpstat.domains.utils.VersionUtils;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

import com.serpstat.core.ToolRegistry;
import com.serpstat.core.SerpstatApiClient;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.EnumSet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Main class for the MCP server for Serpstat API.
 * Architecture: modular with automatic tool registration.
 */
public class SerpstatMcpServer {

    private static final String HOST_ENV = "SERPSTAT_MCP_HOST";
    private static final String PORT_ENV = "SERPSTAT_MCP_PORT";
    private static final String BASE_URL_ENV = "SERPSTAT_MCP_BASE_URL";
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;
    private static final String MESSAGE_ENDPOINT = "/messages";
    private static final String EVENTS_ENDPOINT = "/sse";
    private static final String RELATIVE_BASE_URL_VALUE = "relative";

    private final String apiToken;
    private McpSyncServer mcpServer;
    private Server server;

    public SerpstatMcpServer(String apiToken) {
        this.apiToken = apiToken;
    }

    public static void main(String[] args) {

        final String envName = "SERPSTAT_API_TOKEN";
        String apiToken = null;

        try {
            apiToken = System.getenv(envName);
            if (apiToken == null || apiToken.isEmpty()) {
                System.err.printf("Error: Environment variable %s is not set or empty. Check README.md for instructions.%n", envName);
                System.exit(1);
            }
        } catch (SecurityException e) {
            System.err.printf("Error: Unable to access environment variable %s due to security restrictions.%n", envName);
            System.exit(1);
        }


        SerpstatMcpServer server = new SerpstatMcpServer(apiToken);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
    }

    public void start() throws Exception {
        String host = resolveHost();
        int port = resolvePort();
        String defaultBaseUrl = String.format("http://%s:%d", host, port);
        String baseUrl = resolveBaseUrl(defaultBaseUrl);

        var transportProvider = HttpServletSseServerTransportProvider.builder()
                .objectMapper(new ObjectMapper())
                .baseUrl(baseUrl)
                .messageEndpoint(MESSAGE_ENDPOINT)
                .sseEndpoint(EVENTS_ENDPOINT)
                .build();

        this.server = new Server(new InetSocketAddress(host, port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addFilter(new FilterHolder(new HeadSsePingFilter(EVENTS_ENDPOINT)),
                EVENTS_ENDPOINT,
                EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(new ServletHolder(transportProvider), "/*");
        this.server.setHandler(context);

        this.server.start();

        // Create API client
        SerpstatApiClient apiClient = new SerpstatApiClient(apiToken);

        // Create a tool registry and automatically register all tools
        ToolRegistry toolRegistry = new ToolRegistry(apiClient);

        // Create and configure an MCP server
        this.mcpServer = McpServer.sync(transportProvider)
                .serverInfo("serpstat-mcp-server", VersionUtils.getVersion())
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .build();

        // Automatically register all tools
        toolRegistry.registerAllTools(mcpServer);

        System.err.println("🚀 Serpstat MCP Server started successfully!");
        System.err.printf("📊 Registered %d tools across %d domains%n",
                toolRegistry.getToolCount(), toolRegistry.getDomainCount());
        System.err.printf("⚙️  Configuration -> host: %s (env %s), port: %d (env %s)%n", host, HOST_ENV, port, PORT_ENV);
        System.err.printf("🌐 SSE transport available at %s%s%n", defaultBaseUrl, EVENTS_ENDPOINT);
        String advertisedMessageEndpoint = baseUrl.isEmpty()
                ? MESSAGE_ENDPOINT
                : baseUrl + MESSAGE_ENDPOINT;
        System.err.printf("📨 Message endpoint advertised as %s%n", advertisedMessageEndpoint);
        System.err.println("⏳ Waiting for MCP client connections over SSE...");

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Block the main thread until Jetty is stopped
        server.join();
    }

    private void shutdown() {
        System.err.println("🛑 Shutting down Serpstat MCP Server...");

        if (server != null) {
            try {
                server.stop();
                System.err.println("✅ Jetty server stopped.");
            } catch (Exception e) {
                System.err.println("❌ Error while stopping Jetty server: " + e.getMessage());
            } finally {
                try {
                    server.destroy();
                    System.err.println("🧹 Jetty server resources released.");
                } catch (Exception e) {
                    System.err.println("❌ Error while destroying Jetty server: " + e.getMessage());
                }
            }
        }

        try {
            if (mcpServer != null) {
                mcpServer.closeGracefully();
                System.err.println("✅ MCP server closed gracefully.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error during MCP shutdown: " + e.getMessage());
        }
    }

    private String resolveHost() {
        String envHost = System.getenv(HOST_ENV);
        if (envHost == null || envHost.isBlank()) {
            return DEFAULT_HOST;
        }
        return envHost.trim();
    }

    private int resolvePort() {
        String envPort = System.getenv(PORT_ENV);
        if (envPort == null || envPort.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            int parsedPort = Integer.parseInt(envPort.trim());
            if (parsedPort <= 0 || parsedPort > 65535) {
                System.err.printf("⚠️  Port '%s' from %s is out of range. Falling back to default %d.%n",
                        envPort, PORT_ENV, DEFAULT_PORT);
                return DEFAULT_PORT;
            }
            return parsedPort;
        } catch (NumberFormatException e) {
            System.err.printf("⚠️  Invalid port '%s' in %s. Falling back to default %d.%n", envPort, PORT_ENV, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private String resolveBaseUrl(String defaultBaseUrl) {
        String envBaseUrl = System.getenv(BASE_URL_ENV);
        if (envBaseUrl == null || envBaseUrl.isBlank()) {
            return defaultBaseUrl;
        }

        String trimmed = envBaseUrl.trim();
        if (trimmed.isEmpty()) {
            System.err.printf("⚠️  %s is set but empty. Falling back to default %s.%n", BASE_URL_ENV, defaultBaseUrl);
            return defaultBaseUrl;
        }

        if (RELATIVE_BASE_URL_VALUE.equalsIgnoreCase(trimmed)) {
            System.err.printf("🔁 %s=relative → advertising message endpoint as relative path.%n", BASE_URL_ENV);
            return "";
        }

        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        System.err.printf("🌐 Using custom base URL from %s: %s%n", BASE_URL_ENV, trimmed);
        return trimmed;
    }

    private static final class HeadSsePingFilter implements Filter {
        private final String sseEndpoint;

        private HeadSsePingFilter(String sseEndpoint) {
            this.sseEndpoint = sseEndpoint;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // No initialization required.
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            if (request instanceof HttpServletRequest httpRequest
                    && response instanceof HttpServletResponse httpResponse
                    && "HEAD".equalsIgnoreCase(httpRequest.getMethod())
                    && matchesEndpoint(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setContentType("text/event-stream");
                httpResponse.setHeader("Cache-Control", "no-cache");
                httpResponse.setContentLength(0);
                return;
            }

            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            // No cleanup required.
        }

        private boolean matchesEndpoint(HttpServletRequest request) {
            String requestUri = request.getRequestURI();
            if (requestUri == null) {
                return false;
            }

            if (requestUri.equals(sseEndpoint)) {
                return true;
            }

            if (!sseEndpoint.endsWith("/") && requestUri.equals(sseEndpoint + "/")) {
                return true;
            }

            return false;
        }
    }
}
