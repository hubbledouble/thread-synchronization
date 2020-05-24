# Thread Synchronization
Ensures two or more concurrent thread processes be synchronized so they don't execute simultaneously critical code sections. Using this library makes it easy to focus on the business process than worrying about performing thread synchronization while executing the same process code in two or more distributed nodes.  
  
For more about thread synchronization, please visit [here](https://en.wikipedia.org/wiki/Synchronization_(computer_science)#Thread_or_process_synchronization)  
   
     
Usage example:  
Import the following library
```xml
<dependency>
	<groupId>com.hubbledouble</groupId>
	<artifactId>thread-synchronization</artifactId>
	<version>1.0.0</version>
</dependency>
```
  
<br />    
External requirements:
- Mongo database (this libreary will write to it for synchronizing the processes)
- Spring Mongo Data dependency
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
  
<br />    
Sample java code:  
```java
import com.hubbledouble.thread.synchronization.ThreadSynchronization;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

public class Application {

    private static final ThreadSynchronization THREAD_SYNCHRONIZATION = threadSynchronization(mongoTemplate());

    public static MongoTemplate mongoTemplate() {

        MongoClient mongo = MongoClients.create();
        return new MongoTemplate(mongo, "databaseName");

    }

    public static ThreadSynchronization threadSynchronization(MongoOperations mongoOperations) {
        return new ThreadSynchronization(mongoOperations);
    }

    public static void main(String[] args) {

        THREAD_SYNCHRONIZATION.execute("processName", () ->
                System.out.println("Hello synchronized world!")
        );

    }

}
```
