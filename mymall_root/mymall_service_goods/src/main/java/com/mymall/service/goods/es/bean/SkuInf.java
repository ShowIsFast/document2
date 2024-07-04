package com.mymall.service.goods.es.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 实体类
 * @author Administrator
 *
 */
@Document(indexName="sku",type = "doc")
public class SkuInf implements Serializable{

	@Id
	private String skuId;				//商品id
	@Field(analyzer = "ik_smart",type= FieldType.Text)
	private String name;				//SKU名称
	@Field(type= FieldType.Integer)
	private Integer price;				//价格（分）
	@Field(type= FieldType.Text)
	private String image;				//商品图片
	@Field(type= FieldType.Date)
	private java.util.Date createTime;	//创建时间
	@Field(type= FieldType.Keyword)
	private String categoryName;		//类目名称
	@Field(type= FieldType.Keyword)
	private String brandName;			//品牌名称
	private Map<String, Object> spec;	//规格
	@Field(type= FieldType.Integer)
	private Integer saleNum;			//销量
	@Field(type= FieldType.Integer)
	private Integer commentNum;			//评论数
	@Field(type= FieldType.Text)
	private String spuId;

	public Map<String, Object> getSpec() {
		return spec;
	}

	public void setSpec(Map<String, Object> spec) {
		this.spec = spec;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Integer getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public String getSpuId() {
		return spuId;
	}

	public void setSpuId(String spuId) {
		this.spuId = spuId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return "SkuInf{" +
				"skuId='" + skuId + '\'' +
				", name='" + name + '\'' +
				", price=" + price +
				", image='" + image + '\'' +
				", createTime=" + createTime +
				", categoryName='" + categoryName + '\'' +
				", brandName='" + brandName + '\'' +
				", spec=" + spec +
				", saleNum=" + saleNum +
				", commentNum=" + commentNum +
				", spuId='" + spuId + '\'' +
				'}';
	}

}
