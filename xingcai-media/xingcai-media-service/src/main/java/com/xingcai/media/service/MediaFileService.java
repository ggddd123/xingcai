package com.xingcai.media.service;

import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.base.model.RestResponse;
import com.xingcai.media.model.dto.QueryMediaParamsDto;
import com.xingcai.media.model.dto.UploadFileParamsDto;
import com.xingcai.media.model.dto.UploadFileResultDto;
import com.xingcai.media.model.po.MediaFiles;

import java.io.File;


public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


    //上传文件
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName);

    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);


    //检查文件是否存在
    public RestResponse<Boolean> checkFile(String fileMd5);

    //检查分块是否存在
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);


    //上传分块
    public RestResponse uploadChunk(String fileMd5,int chunk,String path);


    //分块合并
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);


    public File downloadFileFromMinIo(String bucket, String objectName);

    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);


    MediaFiles getFileById(String mediaId);
}