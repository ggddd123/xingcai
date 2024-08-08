import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.util.ArrayList;
import java.util.List;

public class MinioTest {



    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("root", "12345678")
                    .build();

    //上传文件
    @Test
    public  void upload() {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
//                    .object("test001.mp4")
                    .object("test.png")//添加子目录
                    .filename("C:\\Users\\Lenovo\\Pictures\\Screenshots\\屏幕截图 2023-03-30 142127.png")
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }

    @Test
    public void delete(){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket("testbucket").object("test.png").build());
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }


    //查询文件
    @Test
    public void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test.png").build();
        try(
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(new File("E:\\test.png"));

        ) {
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //将分块文件上传到minion
    @Test
    public void uploadChunk(){
        for (int i = 0; i < 2; i++) {
            try {
                UploadObjectArgs testbucket = UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("chunk/"+i)//添加子目录
                        .filename("E:\\testChunk\\"+i)
                        .build();
                minioClient.uploadObject(testbucket);
                System.out.println("上传分块"+i+"成功");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("上传失败");
            }
        }
    }
    @Test
    public void testMerge() throws Exception{
        List<ComposeSource> sources=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ComposeSource testbucket = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
            sources.add(testbucket);
        }
        //指定合并后的信息
        ComposeObjectArgs testbucket = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)//指定源文件
                .build();
        minioClient.composeObject(testbucket);

    }



}
