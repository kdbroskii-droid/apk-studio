@echo off
setlocal
cd /d "%~dp0"
echo ===========================================
echo APK Studio - Claude Code + MCP Bridge
echo ===========================================
where node >nul 2>nul
if errorlevel 1 (
  echo Node.js 18 or newer is required. Install Node.js, then reopen this file.
  pause
  exit /b 1
)
where claude >nul 2>nul
if errorlevel 1 (
  echo Claude Code CLI was not found.
  echo Install it with: npm install -g @anthropic-ai/claude-code
  echo Then run claude once and sign in with your eligible Claude Pro/Max subscription.
  pause
  exit /b 1
)
if not exist node_modules (
  echo Installing MCP dependencies...
  call npm install
  if errorlevel 1 (
    echo Dependency installation failed.
    pause
    exit /b 1
  )
)
echo Checking Claude Code...
claude --version
echo.
echo Starting the local bridge. Allow Node.js through Windows Firewall on your PRIVATE network if prompted.
echo Keep this window open. Phone and PC must be on the same Wi-Fi.
echo.
node bridge/server.js
pause
