import org.apache.log4j.Logger;

public class Logger4 {

    private static Logger logger=Logger.getLogger(Logger4.class.getName());
    public static void main(String[] args) throws  Exception{
        int index=0;
        while (true){
            Thread.sleep(1000);
            logger.info("index is :"+index++);
        }

    }

}
