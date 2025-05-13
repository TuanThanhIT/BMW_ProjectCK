package vn.iotstar.model;

import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.MilkTeaType;

import java.math.BigDecimal;

public class MilkTeaDto {
	private Integer milkTeaID;
    private String milkTeaName;
    private MilkTeaType milkTeaType; 
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String description;
    private String introduction;
    private Integer stockQuantity;

    private MultipartFile[] images; // Dùng mảng để nhận nhiều ảnh
	public String getMilkTeaName() {
		return milkTeaName;
	}
	public void setMilkTeaName(String milkTeaName) {
		this.milkTeaName = milkTeaName;
	}
	public MilkTeaType getMilkTeaType() {
		return milkTeaType;
	}
	public void setMilkTeaType(MilkTeaType milkTeaType) {
		this.milkTeaType = milkTeaType;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(BigDecimal discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public MultipartFile[] getImages() {
		return images;
	}
	public void setImages(MultipartFile[] images) {
		this.images = images;
	}
	public Integer getMilkTeaID() {
		return milkTeaID;
	}
	public void setMilkTeaID(Integer milkTeaID) {
		this.milkTeaID = milkTeaID;
	}
	public Integer getStockQuantity() {
		return stockQuantity;
	}
	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	public MilkTeaDto() {
	}
    
    
}