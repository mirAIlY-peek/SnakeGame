### Windows - Snake

follow this code on windows

    cd CommandLine\Modular\CLI\hellofx
    set PATH_TO_FX="path\to\javafx-sdk-13\lib"
    set PATH_TO_FX_MODS="path\to\javafx-jmods-13"
    dir /s /b src\*.java > sources.txt & javac --module-path %PATH_TO_FX% -d mods/hellofx @sources.txt & del sources.txt

run:
    
    java --module-path "%PATH_TO_FX%;mods" -m hellofx/hellofx.Game