@echo off
echo Compilation script for SceneBuilder Java3D
if "%1"=="" goto error

echo Please be patient, compiling will take some time...
echo Now compiling ch\isbiel\scenebuilder\SceneBuilder.java
javac -d %1 ch\isbiel\scenebuilder\SceneBuilder.java
echo Now compiling ch\isbiel\scenebuilder\j3dproperty\*.java
javac -d %1 ch\isbiel\scenebuilder\j3dproperty\*.java
echo Now compiling ch\isbiel\scenebuilder\j3dproperty\advanced\*.java
javac -d %1 ch\isbiel\scenebuilder\j3dproperty\advanced\*.java
echo Now compiling ch\isbiel\scenebuilder\j3dobjects\*.java
javac -d %1 ch\isbiel\scenebuilder\j3dobjects\*.java
echo creating directory %1\ch\isbiel\scenebuilder\gui\images
mkdir %1\ch\isbiel\scenebuilder\gui\images
echo copying image files to %1\ch\isbiel\scenebuilder\gui\images
copy ch\isbiel\scenebuilder\gui\images %1\ch\isbiel\scenebuilder\gui\images
echo copying Help.html to %1\ch\isbiel\scenebuilder
copy ch\isbiel\scenebuilder\Help.html %1\ch\isbiel\scenebuilder
echo copying Copyright.html to %1\ch\isbiel\scenebuilder
copy ch\isbiel\scenebuilder\Copyright.html %1\ch\isbiel\scenebuilder
echo copying simplecodetemplate.txt to %1\ch\isbiel\scenebuilder\codegen
copy ch\isbiel\scenebuilder\codegen\simplecodetemplate.txt %1\ch\isbiel\scenebuilder\codegen
echo copying ClassRessources.properties to %1\ch\isbiel\scenebuilder\j3danalyze
copy ch\isbiel\scenebuilder\j3danalyze\ClassRessources.properties %1\ch\isbiel\scenebuilder\j3danalyze
echo copying PropertyEditorCreater.properties to %1\ch\isbiel\scenebuilder\j3dproperty
copy ch\isbiel\scenebuilder\j3dproperty\PropertyEditorCreater.properties %1\ch\isbiel\scenebuilder\j3dproperty
goto end

:error
echo 
echo Usage: compile.bat output_path
echo               to recompile the project SceneBuilder Java3D 
echo               and write the all class files to output_path
echo               give an *existing* output_path with *write permission*
goto end

:end
