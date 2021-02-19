# COA2020

## 1.实验要求

### 1.1 Fetch

在Cache类中实现fetch方法将数据从内存读到Cache(如果还没有加载到Cache)，并返回被更新的Cache行的行号(需要调用不同的映射策略和替换策略)

``` java
 String fetch(String sAddr, int len);
```

### 1.2 Write

在Cache类中实现write方法

```java
void write(String eip, int len, char[] data);
```

### 1.3 Strategy

实现映**组关联映射**策略、**先进先出**替换策略、**WriteBack**写策略

## 2.相关资料

### 设计模式

我们给出了映射策略、替换策略、写策略的抽象类。虽然我们只要求实现部分的策略，但是我们依然采用了策略模式，这样如果大家对实现别的策略感兴趣，只需要实现相应的接口，而不需要修改其他的代码。

#### 策略模式

以替换策略为例，如果不使用接口，而是直接实现一个具体的`FIFOReplacement`类，那么在Cache的代码中，所有类型为`ReplacementStrategy`的字段其实头可以替换成`FIFOReplacement`，但是这样代码的拓展性、可修改性就比较差。因为如果我们要求大家实现其他的策略，比如`LRU`，那么你不仅要去实现一个新的`LRUReplacement`类，还要修改其他地方的代码，不同组件之间的耦合性会增加。我们希望通过**只增加代码而不修改代码**来对系统进行拓展，**高内聚、低耦合**是我们的目标。

> 耦合可以完全消除吗？不可以，只要存在引用，就会存在耦合。当然，大家可以通过IOC来进一步的降低耦合，不过这已经超出了本门课以及实验会涉及的范围。

#### 单例模式

大家都写过JVM的话一定知道类加载的过程是怎么样的，那么单例模式就很好理解了，我们将类的构造方法私有化，再添加一个该类类型的静态字段，对于使用该类的使用者们来说，它们看到的永远是相同的对象，为什么要使用单例模式？因为我们使用了OO编程语言，但是在真实的计算机系统当中，很多部件都是唯一的，所以我们需要通过单例模式来保证主存、CPU、Cache等的唯一性。大家也可以通过使用单例模式或者巧妙使用静态字段实现一个CPU中的寄存器模型，类似于C语言中的union。

```java
public A{
  
  	private A() {}
		private static A a = new A();
  	public static A getA() { return a; }
  
}
```

### 高速缓存存储器

![image-20201112233657063](https://pkun.oss-cn-beijing.aliyuncs.com/uPic/WJ0Edi.png)

在一个计算机系统中，如果有$m$条地址线，我们就能构成$2^m$个不同的地址，由于CPU和主存之间的性能差距不断增大，设计者引入了一个高速缓存器，在满足某些条件的情况下，它可以有效的提高计算机系统的运行效率。这是得益于程序的局部性。目前大部分计算机系统的存储结构以及性能如上图所示。

一般性的，一个Cache可以由一个四元组组成$(S, E, B, m)$，$S$表示$Cache$的组数，$E$表示每组的行数，$B$表示每行能表示多少个字节，$m$则是物理地址的长度。

这种层次性的存储策略应用场景非常的广泛。拿大家下学期会学习的软工二来说，在构建一个Web后端系统的时候，我们往往是从关系型数据库中读取信息，比如Mysql。但是Mysql的数据默认都是以B树或者B+树的结构存储在磁盘上的，多次访问磁盘不仅寻道、定位扇区速度缓慢，还会磁盘IO的带宽有一定的要求。不过现在Mysql和其他数据库往往自带缓存机制，如果两次查询语句是相同的，那么可以直接从内存中返回数据。同时我们还可以使用类似Redis或者MemoryCache这样的中间件来将数据存到内存之中，加速访问。当然，如果你使用Redis，那么性能的瓶颈就不是数据存储而是进程通信或者网路阻塞了。

## 3.实验攻略

请认真阅读任老师的PPT，任何与具体实现相关的知识应该已经体现在PPT之中了，README里面只会涉及课堂之外的内容。另外，为了降低实验难度，我们不要求大家实现所有的替换策略，只需要实现FIFO即可。

##### 数据结构

CacheLine表示Cache中的一行，CacheLinePool就是行的集合

```java
	private class CacheLine {
		// 有效位，标记该条数据是否有效
		boolean validBit = false;
		// 脏位，标记该条数据是否被修改
		boolean dirty = false;
		// 用于LFU算法，记录该条cache使用次数
		int visited = 0;

		// 用于LRU和FIFO算法，记录该条数据时间戳
		Long timeStamp = 0l;

		// 标记，占位长度为()22位，有效长度取决于映射策略：
		// 直接映射: 12 位
		// 全关联映射: 22 位
		// (2^n)-路组关联映射: 22-(10-n) 位
		// 注意，tag在物理地址中用高位表示，如：直接映射(32位)=tag(12位)+行号(10位)+块内地址(10位)，
		// 那么对于值为0b1111的tag应该表示为0000000011110000000000，其中前12位为有效长度，
		// 因为测试平台的原因，我们无法使用4GB的内存，但是我们还是选择用32位地址线来寻址
		char[] tag = new char[22];
		// 数据
		char[] data = new char[LINE_SIZE_B];
	}
```

```java
private class CacheLinePool {
   /**
    * @param lines Cache的总行数
    */
   CacheLinePool(int lines) {
      clPool = new CacheLine[lines];
   }
   private CacheLine[] clPool;
}
```

由于组关联映射可以实现直接映射和关联映射的效果，所以我们在测试中调用了set方法来让组关联达到了直接映射和关联映射的效果，请大家不要随意修改，作业中默认是256个组，每组4行，大家也可以尝试其他的组合策略，但是对测试的时间不会产生太大的影响。

> 在针对组关联Cache读数据是否正确的测试用例中，我们会调用Set方法修改大家的SET和setSize

```java
public class SetAssociativeMapping extends MappingStrategy{

    private int SETS=256; 		// 共256个组
    private int setSize=4;   // 每个组4行
}
```

代码框架并不是已经钉死的东西，同学们需要自己对数据结构和方法进行**修改和补充**。

#### 源码阅读

故事要从$Cache$的读写开始说起。由于$Cache$需要进行分块读写，所以我们提供了一个`helper`函数，这个函数已经写好了，他做的事情就是分块读写，然后根据参数来判断究竟是读还是写。在`helper`中，我们调用了`fetch`函数，这个函数会检查$Cache$是否包含某一个特定的$block$，如果检查到了就会返回，没有检查到就会从内存中加载。因为我们只需要实现一种替换策略，所以这里你可以硬编码一个`FIFO`进去，对于映射策略来说，本质上只有一种映射策略。

```java
	private char[] helper(String eip, int len, char[] writeData){
		char[] data = new char[len];
		Transformer t = new Transformer();
		int addr =  Integer.parseInt(t.binaryToInt("0" + eip));
		int upperBound = addr + len;
		int index = 0;
		while (addr < upperBound) {
			int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
			if (addr + nextSegLen >= upperBound) {
				nextSegLen = upperBound - addr;
			}
			int i=0;
			if(writeData == null){
				int rowNO = fetch(t.intToBinary(String.valueOf(addr)), nextSegLen);
				char[] cache_data = cache.get(rowNO).getData();
				while (i < nextSegLen) {
					data[index] = cache_data[addr % LINE_SIZE_B + i];
					index++;
					i++;
				}
			}
			else{
				int rowNO = fetch(t.intToBinary(String.valueOf(addr)), nextSegLen);
				char[] input = new char[LINE_SIZE_B];
				Arrays.fill(input, '0');
				while (i < nextSegLen) {
					input[addr % LINE_SIZE_B + i] = writeData[index];
					index++;
					i++;
				}
				writeStrategy.write(rowNO, input);
			}
			addr += nextSegLen;
		}
		return data;
	}
```

请同学们编码之前仔细阅读这段代码，这会帮助你理解整个$Cache$运行的流程，也会帮助你进行编码。

#### 写回

如果只是读数据，那么本次作业就非常的简单，但是加入了写数据的内容后，一切都变的复杂了起来，在虚存的章节，大家会了解到$Cache、Memory、Disk$之间存在一定的一致性，会不会出现$Cache$被修改了，主存也被修改了，但是两次修改的数据不一样呢？这个时候该以哪一份数据为准呢？当然，在本次作业中，除了测试需要，我们不会主动修改主存，那么这意味着主存只会在两种情况下修改

- 测试需要
- $Cache$写回

本次作业中也只有一个地方会$Cache$对内存的写回。对于写回来说，需要在$CacheLine$中添加一个脏位来表示这个数据是否已经被修改过了。脏位和ValidBit在测试中也会被测试，这两个Bit之间是存在一定的约束的。**写回的时候如果数据没满一行，请不要补0补满一行**

#### 测试

虽然只需要实现1 * 1 * 1 = 1种策略，但是对于组关联来说，他的Set和SetSize可以被修改，所以我们的测试中还是调用了`cache.setStrategy`方法来设置不同的策略，我们会检查访问特定行是否输出了正确的数据，或者访问特定的行检查它的Tag。我们也会检查不同时刻$Cache$和$Memory$中的数据是否正确等。

#### 编码 😈

为了减轻大家的负担，我们归纳了本次作业中你需要完成的小任务以及步骤

- 实现好替换策略和映射策略
- 在$Cache$中初始化好替换策略和映射策略
- 编写好`fetch`

到此为止，如果你的代码正确，你应该可以通过所有读用例

- 实现写回操作相关的代码

到此为止，如果你的代码正确，你应该可以通过所有写用例

#### 彩蛋

在最后一个测试用例中，我们会将一段特定的信息写入$Cache$，这段信息表明了一串数据的位置，这串数据被进行了加密，保存在内存中。把你解密完的信息存到$Cache$中，保持地址不变，我们会检查信息是否被正确的翻译了。如果数据被修改但是错误，你将不会通过这个测试，如果你得到了正确的答案，那么你会通过测试，如果你什么都不做，一样会通过测试~GOOD LUCK HAVE FUN！

> 注意：如果你不想做彩蛋却不小心修改了某些和彩蛋相关的内容会导致最后的评分不是满分

## 4.代码框架

```java
.
├── README.md    # This File
├── pom.xml      # Maven Config File
└── src          
    └── main
       └── java
           ├── memory
           │   ├── Cache.java												# Cache代码
           │   ├── Memory.java 											# 主存，这次作业不需要修改
           │   ├── MemoryInterface.java							# 主存接口
           │   ├── cacheMappingStrategy							# 映射策略
           │   │   ├── MappingStrategy.java
           │   │   └── SetAssociativeMapping.java
           │   ├── cacheReplacementStrategy         # 替换策略
           │   │   ├── FIFOReplacement.java
           │   │   └── ReplacementStrategy.java
           │   └── cacheWriteStrategy								# 写回策略
           │       ├── WriteBackStrategy.java
           │       └── WriteStrategy.java
           ├── transformer
           │   └── Transformer.java
           └── util
               ├── BinaryIntegers.java
               └── IEEE754Float.java

```

## 5.未尽事宜

请写邮件给任老师

