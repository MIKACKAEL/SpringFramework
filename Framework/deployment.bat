@echo off
setlocal

rem Extraire le nom du dossier root à partir du chemin du dossier source
for %%I in (".") do set "projet=%%~nxI"

set temp=.\temp
set web=..\test
set conf=.\conf
set lib=.\lib
set src=.\src
set bin=.\bin
set webappFolder=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps

rem création du dossier temporaire
if exist "%temp%" (
    rd /S /Q "%temp%"
    mkdir "%temp%"
) else (
    mkdir "%temp%"
)



rem Copie des élements indispensables pour tomcat vers temp
copy /Y  ".\index.jsp" "%temp%"
xcopy /E /I /Y "%web%\" "%temp%\WEB-INF\"

rem Compilation des codes java vers le dossier bin
call compilateur.bat

cd /D "%bin%"

jar -cvf "..\lib\%projet%".jar *

cd /D ..\

xcopy /E /I /Y "%lib%\" "%temp%\WEB-INF\lib"


rem Copie des élements de bin vers classes de tomcat
xcopy /E /I /Y "%bin%\" "%temp%\WEB-INF\classes"

rem Déplacement du répertoire actuel vers temp
cd /D "%temp%"

rem Compresser dans un fichier jar
jar -cvf "..\%projet%".war *

rem Déplacement du répertoire actuel vers le projet
cd /D ..\

rem delete temp dir
rd /S /Q "%temp%"

rem Copie des élements indispensables pour tomcat vers temp
copy /Y ".\%projet%.war" "%webappFolder%"

rem delete .war file
del ".\%projet%.war"

echo Déploiement terminé.

endlocal
