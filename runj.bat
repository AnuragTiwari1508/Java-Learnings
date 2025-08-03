@echo off
setlocal enabledelayedexpansion

REM Find the latest Red Hat Java extension
for /f "delims=" %%i in ('dir "%USERPROFILE%\.vscode\extensions\redhat.java-*" /b /o:-n 2^>nul ^| findstr /r "redhat\.java-.*win32-x64$"') do (
    set "JAVA_EXT=%%i"
    goto :found
)

:found
if not defined JAVA_EXT (
    echo Error: Red Hat Java extension not found!
    echo Please make sure the Java extension is installed in VS Code.
    pause
    exit /b 1
)

echo Using Java extension: !JAVA_EXT!

REM Find the JRE folder inside the extension
for /f "delims=" %%j in ('dir "%USERPROFILE%\.vscode\extensions\!JAVA_EXT!\jre\*" /b /a:d 2^>nul') do (
    set "JRE_VERSION=%%j"
    goto :compile
)

:compile
if not defined JRE_VERSION (
    echo Error: JRE not found in the Java extension!
    pause
    exit /b 1
)

set "JAVA_PATH=%USERPROFILE%\.vscode\extensions\!JAVA_EXT!\jre\!JRE_VERSION!\bin"

echo Compiling %1.java...
"!JAVA_PATH!\javac.exe" %1.java
if !errorlevel! neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running %1...
"!JAVA_PATH!\java.exe" %1

endlocal
