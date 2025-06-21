# Serpstat MCP Server Installation Guide for Windows

Welcome! This guide will help you install the Serpstat MCP Server on your Windows computer and connect it to Claude Desktop. Don't worry - we'll walk through each step together.

## What You'll Need

- A Windows computer
- A Serpstat subscription with API access (Team plan or higher)
- A Claude Pro or Teams subscription (free version has limited usage)
- About 10-15 minutes of your time

## Step 1: Install Claude Desktop

1. **Download Claude Desktop** from the official website:
    - Go to https://claude.ai/download
    - Click the "Download for Windows" button
    - Wait for the download to complete

2. **Install Claude Desktop**:
    - Find the downloaded file in your Downloads folder
    - Double-click to run the installer
    - Follow the installation wizard (just click "Next" → "Next" → "Install")
    - Launch Claude Desktop when installation is complete

## Step 2: Install Java

The Serpstat MCP Server needs Java to run. Don't worry - it's free and safe!

1. **Download Java**:
    - Visit: https://learn.microsoft.com/en-us/java/openjdk/download
    - Click on **OpenJDK 17.0.15 LTS**
    - Or use this direct link: https://aka.ms/download-jdk/microsoft-jdk-17.0.15-windows-x64.msi

2. **Install Java**:
    - Double-click the downloaded `.msi` file
    - Follow the installation wizard (accept the defaults)
    - Restart your computer when prompted

3. **Verify Java is installed**:
    - Press `Windows + R` keys
    - Type `cmd` and press Enter
    - In the black window, type: `java -version`
    - You should see something like "openjdk version 17.0.15"
    - If you see an error, try restarting your computer

## Step 3: Download Serpstat MCP Server

1. **Get the latest version**:
    - Go to: https://github.com/vitos73/serpstat-mcp-server-java/releases
    - Look for the latest release (currently v0.0.2)
    - Download the file `serpstat-mcp-server-java-0.0.2.jar`

2. **Create a folder for the server**:
    - Open File Explorer
    - Go to your C: drive
    - Right-click and select "New" → "Folder"
    - Name it `serpstat_mcp`
    - Copy the downloaded `.jar` file into this folder

## Step 4: Get Your Serpstat API Token

You need a paid Serpstat subscription (Team plan or higher) to use the API.

1. **Log into Serpstat**:
    - Go to https://serpstat.com/users/profile/
    - Sign in to your account

2. **Copy your API token**:
    - Look for the "API Token" section
    - Copy the long string of characters
    - **Important**: Keep this token safe - it's like a password!

> **Don't have a Serpstat subscription?** You'll need to purchase one at https://serpstat.com/pricing/

## Step 5: Upgrade Your Claude Subscription

The free Claude plan has very limited usage - you'll need Claude Pro or Teams to generate full SEO reports.

1. **Upgrade Claude**:
    - In Claude Desktop, look for subscription options
    - Choose Claude Pro ($20/month) or Claude Teams
    - This gives you much higher usage limits

## Step 6: Configure Claude Desktop

Now we'll tell Claude how to connect to the Serpstat server.

1. **Open Claude Settings**:
    - In Claude Desktop, click the menu icon (three lines) in the top-right corner
    - Select "Settings"
    - Go to the "Developer" tab
    - Click "Edit Config"

2. **Add the configuration**:
    - Replace everything in the config file with this code:
    - **Remember to replace `PLACE_YOUR_TOKEN_HERE` with your actual Serpstat API token!**

```json
{
  "mcpServers": {
    "serpstat": {
      "command": "java",
      "args": [
        "-Dfile.encoding=UTF-8",
        "-Dconsole.encoding=UTF-8",
        "-jar",
        "c:/serpstat_mcp/serpstat-mcp-server-java-0.0.2.jar"
      ],
      "env": {
        "SERPSTAT_API_TOKEN": "PLACE_YOUR_TOKEN_HERE",
        "JAVA_TOOL_OPTIONS": "-Dfile.encoding=UTF-8"
      }
    }
  }
}
```

3. **Save and restart**:
    - Save the config file
    - Close Claude Desktop completely
    - Open Claude Desktop again

## Step 7: Test the Connection

Let's make sure everything is working properly.

1. **Check for errors**:
    - When Claude starts, look for any error messages
    - If you see errors about Java or the jar file, double-check your file paths

2. **Look for the Serpstat connection**:
    - You should see a small indicator that MCP tools are connected
    - This might appear as a small icon or notification

## Step 8: Create Your First SEO Report!

Now for the fun part - let's generate your first comprehensive SEO report!

### Example Prompts to Try:

**For a complete domain analysis:**
```
Create a comprehensive SEO report for netpeak.bg using Serpstat. Include:
- Domain overview and key metrics
- Top performing keywords
- Competitor analysis
- Regional performance
- Backlinks summary
- Actionable recommendations
```

**For competitive research:**
```
I want to analyze the SEO landscape for e-commerce websites selling shoes. 
Use Serpstat to compare nike.com, adidas.com, and puma.com. 
Show me their organic performance, top keywords, and opportunities.
```

**For keyword research:**
```
Help me build a content strategy around "digital marketing" using Serpstat. 
Find related keywords, analyze competition levels, and suggest 
which keywords I should target first.
```

## Troubleshooting

**Problem**: Claude shows "Connection failed" or similar errors
- **Solution**: Make sure your API token is correct and you have an active Serpstat subscription

**Problem**: Java errors when starting
- **Solution**: Restart your computer and make sure Java 17 is properly installed

**Problem**: File not found errors
- **Solution**: Double-check that the jar file is in `c:/serpstat_mcp/` and the filename matches exactly

**Problem**: SEO reports are incomplete
- **Solution**: Make sure you have Claude Pro/Teams subscription for higher usage limits

## Getting Help

- Check the project repository: https://github.com/vitos73/serpstat-mcp-server-java
- Contact Serpstat support for API-related questions
- Make sure your Serpstat subscription includes API access

## You're All Set! 🎉

Congratulations! You now have a powerful SEO analysis tool connected to Claude. You can generate detailed reports, analyze competitors, research keywords, and much more.

**Pro tip**: Start with smaller analyses to get familiar with the tools, then move on to comprehensive reports. The combination of Claude's AI and Serpstat's data is incredibly powerful for SEO research and strategy development.

Happy analyzing!