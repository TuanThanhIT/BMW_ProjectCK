// Xử lý sự kiện chọn sao
const stars = document.querySelectorAll('.rating-stars');
stars.forEach(star => {
	star.addEventListener('click', () => {
		const selectedValue = star.getAttribute('data-value');
		document.getElementById('selected-rating').value = selectedValue;

		// Làm nổi bật các sao đã chọn
		stars.forEach(star => {
			star.classList.remove('selected');
		});
		for (let i = 0; i < selectedValue; i++) {
			stars[i].classList.add('selected');
		}
	});
});
