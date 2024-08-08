package com.xingcai.media.api;

import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.media.model.dto.QueryMediaParamsDto;
import com.xingcai.media.model.dto.UploadFileParamsDto;
import com.xingcai.media.model.dto.UploadFileResultDto;
import com.xingcai.media.model.po.MediaFiles;
import com.xingcai.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile multipartFile,
                                      @RequestParam(value = "objectName",required = false) String objectName) throws IOException {


        Long companyId=123141425L;

        //创建一个临时文件
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileSize(multipartFile.getSize());
        //图片
        uploadFileParamsDto.setFileType("001001");
        //文件名称
        uploadFileParamsDto.setFilename(multipartFile.getOriginalFilename());//文件名称
        //文件大小
        long fileSize = multipartFile.getSize();
        uploadFileParamsDto.setFileSize(fileSize);

        File minio = File.createTempFile("minio", ".temp");
        multipartFile.transferTo(minio);

        //文件路径
        String absolutePath = minio.getAbsolutePath();


        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath,objectName);
        return uploadFileResultDto;

    }





}
