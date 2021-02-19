## COA2020

在ALU类中实现7个方法(别担心，绝大部分都很简单)，具体如下
注意，所有参数和返回值都是32位的二进制数，其中除了位移运算的src参数为无符号数，其余参数均为有符号的整数补码


1.计算两个32位有符号二进制数的模，dest表示被除数，src表示除数(dest mod src)
``` java
 String imod(String src, String dest)
```

2.逻辑左移运算，其中dest表示操作数(有符号)，src表示移动位数(无符号)，8-12同理
``` java
 String shl(String src, String dest)
```

3.逻辑右移
``` java
 String shr(String src, String dest)
```

4.算数左移
``` java
 String sal(String src, String dest)
```

5.算术右移
``` java
 String sar(String src, String dest)
```

6.循环左移
``` java
 String rol(String src, String dest)
```

7.循环右移
``` java
 String ror(String src, String dest)
```

---

在FPU类中实现2个方法，具体如下

1.计算两个浮点数真值的和，参数与返回结果为32位单精度浮点数。（符号位、指数部分与尾数部分分别为1、8、23位。）
``` java
 String add(String a, String b)
```

2.计算两个浮点数真值的差
``` java
 String sub(String a, String b)
```