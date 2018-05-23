##编译Familia笔记

```
win10 bash
```
修改depends.mk,更好的编译

判断文件夹,避免重复下载

```
if [ ! -d "$(DIR)" ];\
```
适应aclocal当前版本

```
autoreconf -ivf && $(MAKE)
````