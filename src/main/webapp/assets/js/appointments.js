"use strict";
(function () {
    function setMessage(select, message, disabled) {
        select.replaceChildren(new Option(message, ""));
        select.disabled = disabled;
    }

    async function loadAvailableSlots(doctor, date, slots) {
        if (!doctor.value || !date.value) {
            setMessage(slots, "Select doctor and date", true);
            return;
        }
        setMessage(slots, "Loading available times...", true);
        const endpoint = slots.dataset.endpoint;
        const url = endpoint + "?doctorId=" + encodeURIComponent(doctor.value)
            + "&appointmentDate=" + encodeURIComponent(date.value);
        try {
            const response = await fetch(url, {headers: {"Accept": "application/json"}});
            if (!response.ok) {
                if (response.status === 400) throw new Error("INVALID_REQUEST");
                if (response.status === 403) throw new Error("FORBIDDEN");
                throw new Error("SERVICE_ERROR");
            }
            const available = await response.json();
            if (!Array.isArray(available) || available.length === 0) {
                setMessage(slots, "No available slots for this doctor on the selected day", true);
                return;
            }
            const previous = slots.dataset.selected || "";
            const fragment = document.createDocumentFragment();
            fragment.appendChild(new Option("Select a time", ""));
            available.forEach(function (slot) {
                const option = new Option(slot.startTime + " - " + slot.endTime, slot.startTime);
                if (slot.startTime === previous) option.selected = true;
                fragment.appendChild(option);
            });
            slots.replaceChildren(fragment);
            slots.disabled = false;
        } catch (error) {
            console.warn("Available appointment times could not be loaded:", error.message);
            const message = error.message === "FORBIDDEN"
                ? "You are not authorized to view available times"
                : error.message === "INVALID_REQUEST"
                    ? "Select a valid doctor and date"
                    : "Available times could not be loaded. Please try again.";
            setMessage(slots, message, true);
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        const doctor = document.getElementById("doctorId");
        const date = document.getElementById("appointmentDate");
        const slots = document.getElementById("startTime");
        if (!doctor || !date || !slots) return;
        const load = function () { loadAvailableSlots(doctor, date, slots); };
        doctor.addEventListener("change", load);
        date.addEventListener("change", load);
        load();
    });
}());
