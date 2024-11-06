document.addEventListener("DOMContentLoaded", () => {
    const districtSelect = document.getElementById("district");
    const schoolSearchInput = document.getElementById("school-search");
    const schoolResultsList = document.getElementById("school-results");
    const searchButton = document.getElementById("search-button");
    const gradeSelect = document.getElementById("grade");
    const classSelect = document.getElementById("class");
    const submitButton = document.getElementById("submit-button");

    const schoolResultId = document.getElementById("school-result-id");

    districtSelect.addEventListener("change", async () => {
        const districtId = districtSelect.value;

        if (districtId) {
            schoolSearchInput.disabled = false;
            searchButton.disabled = false;

            // 검색 버튼 클릭 시
            searchButton.addEventListener("click", async () => {
                const searchQuery = schoolSearchInput.value;

                if (searchQuery.length >= 2) {
                    try {
                        const response = await fetch("/api/schools?districtId=" + districtId + "&schoolName=" + schoolSearchInput.value);
                        const schools = await response.json();

                        schoolResultsList.innerHTML = "";
                        if (schools.length > 0) {
                            schoolResultsList.style.display = "block";
                            schools.forEach(school => {
                                const listItem = document.createElement("li");
                                listItem.textContent = school.name;
                                listItem.addEventListener("click", () => {
                                    schoolSearchInput.value = school.name;
                                    schoolResultId.value = school.id;
                                    schoolResultsList.style.display = "none";
                                    gradeSelect.disabled = false;
                                    classSelect.disabled = true;
                                    submitButton.disabled = true;
                                });
                                schoolResultsList.appendChild(listItem);
                            });
                        } else {
                            schoolResultsList.style.display = "none";
                        }
                    } catch (error) {
                        console.error('Error fetching schools:', error);
                    }
                } else {
                    schoolResultsList.style.display = "none";
                }
            });

            gradeSelect.disabled = true;
            classSelect.disabled = true;
            submitButton.disabled = true;
        } else {
            schoolSearchInput.disabled = true;
            searchButton.disabled = true;
            schoolResultsList.style.display = "none";
        }
    });

    gradeSelect.addEventListener("change", async () => {
        classSelect.disabled = !gradeSelect.value;

        const response = await fetch(
            "/api/classes?districtId=" + districtSelect.value +
            "&schoolId=" + schoolResultId.value +
            "&grade=" + gradeSelect.value);
        const classes = await response.json();

        classSelect.innerHTML = "";
        classes.forEach(class_ => {
            const option = document.createElement("option");
            option.value = class_.id;
            option.textContent = class_.name;
            classSelect.appendChild(option);
        });

        submitButton.disabled = true;
    });

    classSelect.addEventListener("change", () => {
        submitButton.disabled = !classSelect.value;
    });

    submitButton.addEventListener("click", () => {
        const districtId = districtSelect.value;
        const schoolName = schoolResultId.value;
        const gradeId = gradeSelect.value;
        const classId = classSelect.value;

        if (districtId && schoolName && gradeId && classId) {
            window.location.href = `/timetables?districtId=${districtId}&schoolId=${schoolName}&grade=${gradeId}&classNm=${classId}`;
        }
    });
});
