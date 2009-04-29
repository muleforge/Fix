@echo off

REM Scripts for the QFJ binary distribution

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

set CP=%MULE_HOME%/lib/user/mina-core-1.1.0.jar;%MULE_HOME%/lib/user/slf4j-api-1.3.0.jar;%MULE_HOME%/lib/user/slf4j-jdk14-1.3.0.jar;%MULE_HOME%\lib\user\quickfixj-all-1.3.3.jar;%MULE_HOME%\lib\user\quickfixj-examples-1.3.3.jar

java -cp %CP% quickfix.examples.executor.Executor %~dp0../../../examples/src/main/resources/proxy/executor.cfg

pause

:end
