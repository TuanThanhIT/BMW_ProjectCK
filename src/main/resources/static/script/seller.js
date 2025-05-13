const body = document.querySelector('body'),
      sidebar = body.querySelector('nav'),
      toggle = body.querySelector(".toggle"),
      searchBtn = body.querySelector(".search-box"),
      modeSwitch = body.querySelector(".toggle-switch"),
      modeText = body.querySelector(".mode-text");


toggle.addEventListener("click" , () =>{
    sidebar.classList.toggle("close");
})

searchBtn.addEventListener("click" , () =>{
    sidebar.classList.remove("close");
})

modeSwitch.addEventListener("click" , () =>{
    body.classList.toggle("dark");
    
    if(body.classList.contains("dark")){
        modeText.innerText = "Light mode";
    }else{
        modeText.innerText = "Dark mode";
        
    }
});



function openSidebar() {
    document.getElementById("sidebar").style.width = "250px";
    document.getElementById("overlay").style.display = "block"; // Hiển thị overlay
}

function closeSidebar() {
    document.getElementById("sidebar").style.width = "0";
    document.getElementById("overlay").style.display = "none"; // Ẩn overlay
}
//Tạo combobox chọn địa chỉ
document.addEventListener("DOMContentLoaded", function () {
    const citySelect = document.getElementById("city");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");
    const addressInput = document.getElementById("address");

    if (!citySelect || !districtSelect || !wardSelect || !addressInput) {
        console.error("One or more required elements are missing in the DOM.");
        return;
    }

    const Parameter = {
        url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
        method: "GET",
        responseType: "application/json",
    };

    axios(Parameter).then(function (result) {
        const data = result.data;
        renderCity(data);
    }).catch(function (error) {
        console.error("Error fetching data:", error);
    });

    function renderCity(data) {
        data.forEach(city => {
            const option = new Option(city.Name, city.Id);
            citySelect.appendChild(option);
        });

        citySelect.addEventListener("change", function () {
            districtSelect.innerHTML = '<option value="" selected>Chọn quận huyện</option>';
            wardSelect.innerHTML = '<option value="" selected>Chọn phường xã</option>';

            if (this.value !== "") {
                const selectedCity = data.find(city => city.Id === this.value);
                selectedCity.Districts.forEach(district => {
                    const option = new Option(district.Name, district.Id);
                    districtSelect.appendChild(option);
                });
            }

            updateAddress();
        });

        districtSelect.addEventListener("change", function () {
            wardSelect.innerHTML = '<option value="" selected>Chọn phường xã</option>';
			
			if (this.value !== "") {
			                const selectedCity = data.find(city => city.Id === citySelect.value);
			                const selectedDistrict = selectedCity.Districts.find(district => district.Id === this.value);

			                selectedDistrict.Wards.forEach(ward => {
			                    const option = new Option(ward.Name, ward.Id);
			                    wardSelect.appendChild(option);
			                });
			            }

			            updateAddress();
			        });

			        wardSelect.addEventListener("change", updateAddress);
			    }

			    function updateAddress() {
			        const city = citySelect.options[citySelect.selectedIndex]?.text || "";
			        const district = districtSelect.options[districtSelect.selectedIndex]?.text || "";
			        const ward = wardSelect.options[wardSelect.selectedIndex]?.text || "";

			        addressInput.value = `${ward}, ${district}, ${city}`.replace(/, $/, "").replace(/^, /, "");
			    }
			});

			// Hàm cắt chuỗi trước dấu phẩy đầu tiên
			function getStringBeforeComma(inputString) {
			    if (!inputString) return ""; // Kiểm tra chuỗi rỗng hoặc null
			    const index = inputString.indexOf(","); // Tìm vị trí dấu ","
			    if (index === -1) return inputString; // Nếu không có dấu ",", trả về toàn bộ chuỗi
			    return inputString.substring(0, index); // Cắt chuỗi từ đầu đến dấu ","
			}

			// Lưu giá trị chuỗi trước dấu phẩy vào thẻ hidden
			document.addEventListener("DOMContentLoaded", () => {
			    const imageElement = document.getElementById("milkTeaImage");
			    const hiddenElement = document.getElementById("hiddenValue");
			    const outputElement = document.getElementById("output");

			    // Lấy giá trị từ thuộc tính `data-image`
			    const imageValue = imageElement.getAttribute("data-image");

			    // Cắt chuỗi trước dấu phẩy đầu tiên
			    const result = getStringBeforeComma(imageValue);

			    // Lưu kết quả vào thẻ hidden
			    hiddenElement.value = result;

			    // Hiển thị kết quả (nếu cần)
			    outputElement.textContent = result;

			    console.log("Giá trị lưu trong thẻ hidden:", hiddenElement.value);
			});

			document.addEventListener('DOMContentLoaded', function() {
			    const updateButtons = document.querySelectorAll('.btn-update');

			    updateButtons.forEach(button => {
			        button.addEventListener('click', function(event) {
			            event.preventDefault();
			            const orderId = this.getAttribute('data-order-id');

			            fetch(`/seller/orders/${orderId}/update-status`, {
			                method: 'POST',
			                headers: {
			                    'Content-Type': 'application/json'
			                },
			                body: JSON.stringify({ status: 'COMPLETE' })
			            })
			            .then(response => response.json())
			            .then(data => {
			                if (data.success) {
			                    alert('Cập nhật trạng thái thành công!');
			                    location.reload();
			                } else {
			                    alert('Có lỗi xảy ra, vui lòng thử lại.');
			                }
			            });
			        });
			    });
			});
