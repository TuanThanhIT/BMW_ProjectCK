document.addEventListener("DOMContentLoaded", function() {
	const favoriteButtons = document.querySelectorAll(".favorite-button");

	favoriteButtons.forEach((button) => {
		button.addEventListener("click", function(event) {
			event.preventDefault();

			const milkTeaID = this.closest(".store-overlay")?.querySelector(".milkTeaID")?.value;
			const userID = this.closest(".store-overlay")?.querySelector(".userID")?.value;

			if (!milkTeaID || !userID) {
				alert("Thông tin không đầy đủ. Vui lòng thử lại.");
				return;
			}

			const isFavorite = this.dataset.favorite === "true";

			const requestData = {
				userID: userID,
				milkTeaID: milkTeaID,
				action: isFavorite ? "unlike" : "like"
			};

			// Hiển thị trạng thái xử lý
			this.disabled = true;
			this.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';

			fetch('/user/product/api/favorite', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(requestData)
			})
				.then(response => response.json())
				.then(data => {
					if (data.success) {
						const isFavorite = requestData.action === "like";

						if (isFavorite) {
							this.classList.add("active");
							this.dataset.favorite = "true";
							this.innerHTML = '<i class="fa fa-heart me-2"></i> Đã yêu thích';
							alert("Đã thêm vào danh sách yêu thích!");
						} else {
							this.classList.remove("active");
							this.dataset.favorite = "false";
							this.innerHTML = '<i class="fa fa-heart me-2"></i> Yêu thích';
							alert("Đã bỏ yêu thích thành công!");
							window.location.reload();
						}
					} else {
						alert('Có lỗi xảy ra: ' + (data.error || "Không xác định."));
					}
				})
				.catch(error => {
					console.error('Error:', error);
					alert('Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!');
				})
				.finally(() => {
					this.disabled = false;
				});
		});
	});
});
