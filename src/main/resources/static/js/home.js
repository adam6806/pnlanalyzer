$(document).ready(function () {
    $('.init-datatable').DataTable({
        responsive: true
    });

    setTimeout(function () {
        $(".top-message").fadeOut("slow");
    }, 5000);


});

function deleteModal(id, message) {
    bootbox.confirm({
        title: "Delete Confirmation",
        message: message,
        buttons: {
            cancel: {
                label: 'Cancel',
                className: 'btn-secondary'
            },
            confirm: {
                label: 'Delete',
                className: 'btn-danger'
            }
        },
        centerVertical: true,
        callback: function (result) {
            if (result) {
                $('#' + id).submit();
            }
        }
    });
}

function initFormValidation(formId) {
    $('#' + formId + ' input').each(
        function () {
            var input = $(this);
            var type = input.attr("type");
            if (type === 'text') {
                validateAlphaNumeric(input);
            } else if (type === 'email') {
                validateEmail(input);
            } else if (type === 'password') {
                validatePassword(input);
            }

            //alert('Type: ' + input.attr('type') + 'Name: ' + input.attr('name') + 'Value: ' + input.val());
        }
    );

    $('#' + formId + ' :submit').each(function () {
        var submit = $(this);
        submit.click(function (event) {

            var isValid = true;

            $('form input').each(function () {

                    var input = $(this);
                    var type = input.attr("type");
                var fieldIsValid = false;
                    if (type === 'text') {
                        fieldIsValid = validateAlphaNumeric(input, 'now', this);
                    } else if (type === 'email') {
                        fieldIsValid = validateEmail(input, 'now', this);
                    } else if (type === 'password') {
                        fieldIsValid = validatePassword(input, 'now', this);
                    }

                if (isValid && !fieldIsValid) {
                    isValid = fieldIsValid;
                }
                }
            );

            if (!isValid) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    });
}

function togglePassword(inputId, passwordIconId) {
    var input = $('#' + inputId);
    var icon = $('#' + passwordIconId);
    if (input.prop('type') === 'password') {
        input.prop('type', 'text');
        icon.addClass('fa-eye-slash').removeClass('fa-eye');
    } else {
        input.prop('type', 'password');
        icon.addClass('fa-eye').removeClass('fa-eye-slash');
    }
}

function validateAlphaNumeric(input, when, target) {
    var pattern = /^[a-zA-Z0-9 ]+$/;
    var message = "*Must only contain letters, numbers, and spaces.";
    if (when === 'now') {
        return validate(pattern, input, message, target);
    } else {
        return validateOnKeyUp(pattern, input, message);
    }
}

function validateEmail(input, when, target) {
    var pattern = /^.+@.+\..+$/;
    var message = "*Email must be a valid email address.";
    if (when === 'now') {
        return validate(pattern, input, message, target);
    } else {
        return validateOnKeyUp(pattern, input, message);
    }
}

function validatePassword(input, when, target) {
    var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[\w!@#$%^&*]{8,}$/;
    var message = "*Your password must be at least 8 characters long and contain a lower case letter, uppercase letter, and a number. Optionally it can also contain one of these special characters !@#$%^&*";
    if (when === 'now') {
        return validate(pattern, input, message, target);
    } else {
        return validateOnKeyUp(pattern, input, message);
    }
}

function validateOnKeyUp(pattern, input, message) {
    input.keyup(function (event) {
        return validate(pattern, input, message, event.target);
    });
}

function validate(pattern, input, message, target) {
    var str = target.value.trim();
    if (pattern.test(str)) {
        $("#" + target.id.toString() + "InvalidText").text("");
        $(target).addClass("is-valid").removeClass("is-invalid");
        return true;
    } else {
        $("#" + target.id.toString() + "InvalidText").text(message);
        $(target).addClass("is-invalid").removeClass("is-valid");
        return false;
    }
}