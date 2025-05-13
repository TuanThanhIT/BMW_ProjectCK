function openSidebar() {
    document.getElementById("sidebar").style.width = "250px";
    document.getElementById("overlay").style.display = "block"; // Hiển thị overlay
}

function closeSidebar() {
    document.getElementById("sidebar").style.width = "0";
    document.getElementById("overlay").style.display = "none"; // Ẩn overlay
}