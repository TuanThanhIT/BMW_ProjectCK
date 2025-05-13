function previewImage(event) {
			const input = event.target;
			const previewContainer = document.getElementById("imagePreview");
			const previewImage = previewContainer.querySelector("img");

			// Nếu có ảnh, hiển thị trong khung
			if (input.files && input.files[0]) {
				const reader = new FileReader();
				reader.onload = function(e) {
					previewImage.src = e.target.result;
					previewImage.style.display = "block";
				};
				reader.readAsDataURL(input.files[0]);
			}
		}