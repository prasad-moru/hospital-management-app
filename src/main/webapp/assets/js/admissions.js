document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('admissionForm');
    var bedSelect = document.getElementById('bedId');
    if (!form || !bedSelect) {
        return;
    }

    var department = document.getElementById('departmentId');
    var roomType = document.getElementById('roomType');
    var contextPath = form.dataset.contextPath;
    var previouslySelectedBed = bedSelect.dataset.selected;

    async function loadAvailableBeds() {
        bedSelect.disabled = true;
        bedSelect.replaceChildren(new Option('Loading available beds...', ''));

        var parameters = new URLSearchParams();
        if (department && department.value) {
            parameters.set('departmentId', department.value);
        }
        if (roomType && roomType.value) {
            parameters.set('roomType', roomType.value);
        }

        try {
            var response = await fetch(contextPath + '/admissions/available-beds?' + parameters.toString());
            if (!response.ok) {
                throw new Error('Available-bed request returned HTTP ' + response.status);
            }

            var beds = await response.json();
            bedSelect.replaceChildren();
            beds.forEach(function (bed) {
                var label = 'Room ' + bed.roomNumber + ' / Bed ' + bed.bedNumber
                    + ' / ' + bed.roomType + ' / INR ' + bed.dailyRate + ' per day';
                var option = new Option(label, String(bed.bedId));
                if (previouslySelectedBed && String(bed.bedId) === previouslySelectedBed) {
                    option.selected = true;
                }
                bedSelect.add(option);
            });

            if (!beds.length) {
                bedSelect.add(new Option('No available beds for this department and room type', ''));
            }
            bedSelect.disabled = !beds.length;
        } catch (exception) {
            bedSelect.replaceChildren(new Option('Unable to load available beds', ''));
            bedSelect.disabled = true;
            console.warn('Available beds request failed');
        }
    }

    if (department) {
        department.addEventListener('change', loadAvailableBeds);
    }
    if (roomType) {
        roomType.addEventListener('change', loadAvailableBeds);
    }
    loadAvailableBeds();
});
