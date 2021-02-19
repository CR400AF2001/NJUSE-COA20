## COA2020

在ALU类中实现整数的二进制乘法(要求使用布斯乘法实现)
输入和输出均为32位二进制补码，计算结果直接截取低32位作为最终输出

``` java
 String mul(String src, String dest)
```

在ALU类中实现整数的二进制除法 operand1 ÷ operand2
输入为32位二进制补码，输出为65位0-1字符串(1位溢出(溢出则置为1) + 32位商 + 32位余数)

``` java
 String div(String src, String dest)
```

注意：除数为0，且被除数不为0时要求能够正确抛出ArithmeticException异常
特殊值可以使用BinaryIntegers.java中定义的值
