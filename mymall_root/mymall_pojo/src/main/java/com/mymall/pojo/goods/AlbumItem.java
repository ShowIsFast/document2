package com.mymall.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * 实体类
 * @author Administrator
 *
 */
@Table(name="tb_album_item")
public class AlbumItem implements Serializable {

    @Id
    private Long id;            //编号

    private String url;         //图片访问路径
    private String bucketName;  //oss-bucketName
    private String fileName;    //oss-文件名称
    private String isDelete;    //文件是否已删除（1：已删除；0：未删除）
    private String parentId;    //文件夹ID
    private String typeId;      //商品ID
    private String type;        //图片类型（1：商品sku_id；2：品牌ID）

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getIsDelete() { return isDelete; }
    public void setIsDelete(String isDelete) { this.isDelete = isDelete; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "AlbumItem{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isDelete='" + isDelete + '\'' +
                ", parentId=" + parentId +
                ", typeId='" + typeId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
