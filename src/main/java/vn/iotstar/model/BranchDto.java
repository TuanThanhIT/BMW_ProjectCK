package vn.iotstar.model;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.User;

public class BranchDto {
    private Integer branchID;
    private String branchName;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String introduction;
    private String description;
    private User user;
    private MultipartFile[] images;

    public Integer getBranchID() { // Đổi tên getter
        return branchID;
    }
    public void setBranchID(Integer branchId) { // Đổi tên setter
        this.branchID = branchId;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public LocalTime getOpenTime() {
        return openTime;
    }
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }
    public LocalTime getCloseTime() {
        return closeTime;
    }
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public MultipartFile[] getImages() {
        return images;
    }
    public void setImages(MultipartFile[] images) {
        this.images = images;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public BranchDto() {
        super();
    }
}
