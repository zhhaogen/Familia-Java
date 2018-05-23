# Familia-Java
用于在java理解和调试baidu Familia,用法参见https://github.com/baidu/Familia
### proto java生成 
```
bin/protoc --java_out java proto/config.proto
```
### jni 
生成

```
javah -cp bin -d src/test/jni -force -jni familia.java.util.CMT19937
javah -cp bin -d src/test/jni -force -jni familia.java.util.CMinstdRand0
```
编译

```
gcc -fPIC -D_REENTRANT -I${JAVA_HOME}/include -I//develop/jdk1.6.0_31/include/linux -shared -o hellojni.so HelloJni.c  
```

```
cl -I "%JAVA_HOME%/include" -I "%JAVA_HOME%/include/win32" -LD familia_java_util_CMinstdRand0.cpp -Fex64/CMinstdRand0.dll 
cl -I "%JAVA_HOME%/include" -I "%JAVA_HOME%/include/win32" -LD familia_java_util_CMT19937.cpp -Fex64/CMT19937.dll 
```