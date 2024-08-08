package com.xingcai.content;

import com.xingcai.content.model.dto.CoursePreviewDto;
import com.xingcai.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@SpringBootTest
public class FreemarkerTset {

    @Autowired
    CoursePublishService coursePublishService;

    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到classpath路径
        String path = this.getClass().getResource("/").getPath();
        //指定模版的目录
        configuration.setDirectoryForTemplateLoading(new File(path+"/templates"));
        configuration.setDefaultEncoding("utf-8");
        //得到模版
        Template template = configuration.getTemplate("course_template.ftl");

        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(120L);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("model",coursePreviewInfo);


        String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream stream = IOUtils.toInputStream(s, "utf-8");
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\120.html"));
        IOUtils.copy(stream,fileOutputStream);
    }

}
