## COA2020


在FPU类中实现浮点数的二进制乘法
输入和输出均为32位IEEE-754标准的单精度二进制数，分数部分(23 bits)的计算结果直接截取前23位

``` java
 String mul(String a, String b)
```


在FPU类中实现浮点数的二进制除法a ÷ b
输入和输出均为32位IEEE-754标准的单精度二进制数，分数部分(23 bits)的计算结果直接截取前23位，计算过程使用4位保护位
注意：除数为0，且被除数不为0时要求能够正确抛出ArithmeticException异常
特殊值可以使用IEEE754Float.java中定义的值

``` java
 String div(String a, String b)
```
