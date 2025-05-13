var citis = document.getElementById("city");

var Parameter = {
    url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
    method: "GET",
    responseType: "application/json",
};

var promise = axios(Parameter);
promise.then(function(result) {
    renderCity(result.data);
});

function renderCity(data) {
    for (const x of data) {
        let option = new Option(x.Name, x.Id);
        citis.options[citis.options.length] = option;
        
        // Kiểm tra nếu giá trị `cityId` trong URL trùng với `x.Id`, đánh dấu `selected`
        const selectedCityId = new URLSearchParams(window.location.search).get('cityId');
        if (selectedCityId === x.Id) {
            option.selected = true;  // Giữ lại lựa chọn đã chọn
        }
    }
}

citis.onchange = function() {
    const selectedValue = citis.value; // Lấy giá trị của tỉnh thành
    const selectedText = citis.options[citis.selectedIndex].text; // Lấy text của option đang được chọn

    // Điều hướng trang với cả ID và Tên tỉnh thành
    window.location.href = `/user/branch?cityId=${selectedValue}&cityName=${encodeURIComponent(selectedText)}`;
};

// Sự kiện cho nút "Tất cả"
document.getElementById("allBranchesBtn").addEventListener("click", function() {
    window.location.href = "/user/branch"; // Chuyển hướng về tất cả các chi nhánh
});
