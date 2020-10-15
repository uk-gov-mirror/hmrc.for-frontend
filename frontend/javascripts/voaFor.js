(function ($) {
    'use strict';

    VoaFor.service = function () {
        return 'sending-rental-information';
    };

    VoaFor.showHistoryBackLink = function(){
        $('a#history-back').show();
    };

    VoaFor.printPageShouldPrintOnLoad = function(){
        if($('div.govuk-grid-column-full.print-your-answers').length > 0){
            window.print();
        }
    };

    VoaFor.currencyFields = function(){
        $('<span class=\'pound\'>£</span>').insertBefore('input[type=text].currency');
    };

    VoaFor.errorFocus = function () {
        var $formErrors = $('.form-error');
        if ($formErrors) {
            $formErrors.focus();
        }
    };

    VoaFor.addField = function () {
        $(document).on('click', '.add', function (e) {
            e.preventDefault();
            var id = $(this).closest('.form-group').attr('id');
            id = id.substring(0, id.lastIndexOf('_'));
            $(this).closest('.form-group').clone().insertBefore($(this).closest('.form-group'));
            $('.add').not(':last').css('display', 'none');
            $('.add-group').each(function () {
                $(this).attr('id', id + '_' + $(this).index());
                $(this).find('input').attr('id', id + '_' + $(this).index() + '_text');
                $(this).find('input').attr('name', id + '[' + $(this).index() + ']');
                $(this).find('label').attr('for', $(this).find('input').attr('id'));
                $('.add-group:last input').val('');
            });
            $('.add-group:last input').focus();
            $('.remove').css('display', 'inline-block');
        });
    };

    VoaFor.removeField = function () {
        $(document).on('click', '.remove', function (e) {
            e.preventDefault();
            $(this).closest('.add-group').remove();
            if ($('.add-group').length === 1) {
                $('.add-group:first').find('.remove').css('display', 'none');
            }
            $('.add:last').css('display', 'table');
        });
    };

    VoaFor.changeIds = function (container, index) {
        function changeIdAndLabelId($container, $elem, newIndex) {
            var oldId = $elem.attr('id');
            var newId = oldId.replace(/(\d+)/g, newIndex);
            $elem.attr('id', newId);

            var label = $container.find('label[for=\'' + oldId + '\']');
            if (label !== undefined) {
                var labelForAttr = label.attr('for');
                if (labelForAttr !== undefined) {
                    label.attr('for', newId);
                }
            }
        }
        //change id of first child form-group to maintain valid html with no duplicated ids.
        var $container = $(container);
        var $firstChildFormGroup = $container.find('fieldset:first');
        if ($firstChildFormGroup !== undefined) {
            var idAtrr = $firstChildFormGroup.attr('id');
            if (idAtrr !== undefined) {
                var newId = idAtrr.replace(/(\d+)/g, index);
                $firstChildFormGroup.attr('id', newId);
            }
        }

        $container.find('input, textarea').not('[type="hidden"]').not('[type="radio"]').each(function () {

            var attrFormgroupId = $(this).closest('.form-group').attr('id');

            var attrName = $(this).attr('name'),
                s = $(this).closest('.multi-fields-group').attr('id'),
                o = s.substring(0, s.lastIndexOf('_') + 1),
                st = $(this).closest('.multi-fields-group').find('.form-date-dayMonth').attr('id'),
                st2 = $(this).closest('.multi-fields-group').find('[data-intel]').attr('class');

            $(this).closest('.form-group').attr('id', attrFormgroupId.replace(/(\d+)/g, index));

            changeIdAndLabelId( $container,$(this), index);

            if (attrName.indexOf('street1') > -1 || attrName.indexOf('street2') > -1) {
                $(this).attr('name', attrName.replace(/\[(\d+)\]/g, '[' + index + ']'));
            } else {
                $(this).attr('name', attrName.replace(/(\d+)/g, index));
            }
            $(this).closest('.multi-fields-group').attr('id', o + index);
            if (st) {
                $(this).closest('.multi-fields-group').find('.form-date-dayMonth').attr('id', st.replace(/(\d+)/g, index));
            }
            if (st2) {
                $(this).closest('.multi-fields-group').find('[data-intel]').attr('class', st2.replace(/(\d+)/g, index));
            }
        });

        $container.find('div[data-show-fields-group]').each(function () {
            var dataAttribute = $(this).attr('data-show-fields-group');
            $(this).attr('data-show-fields-group', dataAttribute.replace(/(\d+)/g, index));
        });

        $container.find('input[type="radio"]').not('[type="hidden"]').each(function () {

            var s = $(this).closest('.multi-fields-group').attr('id'),
                o = s.substring(0, s.lastIndexOf('_') + 1);

            $(this).attr('name', $(this).attr('name').replace(/(\d+)/g, index));
            changeIdAndLabelId($container, $(this), index);
            var showFieldAttr = 'data-show-fields';
            if (this.hasAttribute(showFieldAttr)) {
                var dataShowFields = $(this).attr(showFieldAttr);
                $(this).attr(showFieldAttr, dataShowFields.replace(/(\d+)/g, index));
            }

            $(this).closest('.multi-fields-group').attr('id', o + index);
        });
        VoaRadioToggle.radioDataShowField();
        VoaRadioToggle.radioDataShowFields();
        VoaCommon.GdsSelectionButtons();
    };

    VoaFor.addFieldMulti = function () {

        $(document).on('click', '.add-multi-fields', function (e) {
            e.preventDefault();
            var element = $(this).closest('fieldset');
            var limit = parseInt(element.attr('data-limit'), 10);
            var existingCount = element.find('.multi-fields-group').length;
            var $clonedMultiFields = element.find('.multi-fields-group:last').clone();
            $clonedMultiFields.insertAfter(element.find('.multi-fields-group:last'));
            element.find('.multi-fields-group:last p').remove();
            element.find('.multi-fields-group:last input[type!="radio"]').val('');
            element.find('.multi-fields-group:last input[type="radio"]').removeAttr('checked');
            element.find('.multi-fields-group:last .form-date-dayMonth, .multi-fields-group:last .form-group div').removeClass('form-grouped-error');
            VoaFor.changeIds($clonedMultiFields, existingCount);
            element.find('.multi-fields-group:last .chars').text(element.find('.multi-fields-group:last .chars').attr('data-max-length'));
            $('.remove-multi-fields').css('display', 'inline-block');
            element.find('.multi-fields-group:last textarea').val('');
            element.find('.multi-fields-group:last .form-group:first input:eq(0), .multi-fields-group:last .form-group:first textarea:eq(0)').focus();
            element.find('.multi-fields-group:last').removeClass('form-grouped-error');
            element.find('.multi-fields-group:last').find('.form-group').removeClass('has-error');
            element.find('.multi-fields-group:last .intel-alert').addClass('hidden');
            if ($('.multi-fields-group').length === limit) {
                $(this).hide();
                $('.add-hint').show();
            } else {
                $('.add-hint').hide();
                $(this).show();
            }
        });
    };

    VoaFor.addMultiButtonState = function () {
        var $element = $('.add-multi-fields');
        var $group = $('.multi-fields-group');
        var l = parseInt($element.closest('fieldset').attr('data-limit'), 10);
        if ($group.length === l) {
            $element.hide();
        } else {
            $element.show();
        }
    };

    VoaFor.removeFieldMulti = function () {
        $(document).on('click', '.remove-multi-fields', function (e) {
            e.preventDefault();
            var limit = parseInt($(this).closest('fieldset').attr('data-limit'), 10);
            $(this).closest('.multi-fields-group').remove();
            $('.add-hint').hide();
            if ($('.multi-fields-group').length === 1) {
                $('.multi-fields-group:first').find('.remove-multi-fields').css('display', 'none');
            }
            if ($('.multi-fields-group').length < limit) {
                $('.add-multi-fields').show();
            } else {
                $('.add-multi-fields').hide();
            }
            $('.multi-fields-group').each(function (i) {
                VoaFor.changeIds(this, i);
            });
        });

        $('.multi-fields-group:not(:first)').find('.remove-multi-fields').css('display', 'inline-block');


    };

    VoaFor.selectMobile = function () {
        function setSelectSize() {
            if ($(window).width() <= 640) {
                $('.addressList').attr('size', '');
            } else {
                $('.addressList').attr('size', '8');
            }
        }

        $(window).resize(function () {
            setSelectSize();
        });
        setSelectSize();
    };

    VoaFor.rentLength = function () {
        function setLengthLabel(name, spanName) {
            var element = $('[name="' + name + '"]');

            if (element.is(':checked')) {
                if ($('[name="totalRent.rentLengthType"]:checked').val() === 'annual') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelAnnual'));
                }
                if ($('[name="totalRent.rentLengthType"]:checked').val() === 'quarterly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelQuarterly'));
                }
                if ($('[name="totalRent.rentLengthType"]:checked').val() === 'monthly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelMonthly'));
                }
                if ($('[name="totalRent.rentLengthType"]:checked').val() === 'weekly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelWeekly'));
                }
            }
            element.change(function () {
                if ($(this).val() === 'annual') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelAnnual'));
                }
                if ($(this).val() === 'quarterly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelQuarterly'));
                }
                if ($(this).val() === 'monthly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelMonthly'));
                }
                if ($(this).val() === 'weekly') {
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelWeekly'));
                }
            });
        }

        setLengthLabel('totalRent.rentLengthType', 'totalRent-length');
    };

    VoaFor.updateLabelToggle = function () {
        function changeButtonLabel() {
            if ($('.section1 #isAddressCorrect_false').is(':checked') || VoaCommon.getQueryString('edit')) {
                $('.section1 button#continue').text(VoaMessages.textLabel('buttonUpdate'));
            } else {
                $('.section1 button#continue').text(VoaMessages.textLabel('buttonContinue'));
            }
        }

        $('.section1 [name="isAddressCorrect"]').change(function () {
            changeButtonLabel();
        });
        changeButtonLabel();
    };

    VoaFor.isEdit = function () {
        if (VoaCommon.getQueryString('edit')) {
            $('[name="continue_button"]').text(VoaMessages.textLabel('buttonUpdate')).attr('name', 'update_button');
        }
    };

    VoaFor.radioAgreement = function () {
        function radioAgrrementToggle() {
            if (!$('[name="leaseAgreementType"]').is(':checked')) {
                //do nothing
            } else if ($('#leaseAgreementType_verbal').is(':checked')) {
                $('.leaseAgreementVerbal').removeClass('hidden');
                $('.leaseAgreementWritten').addClass('hidden');
            } else {
                $('.leaseAgreementVerbal').addClass('hidden');
                $('.leaseAgreementWritten').removeClass('hidden');
            }
        }

        radioAgrrementToggle();
        $('[name="leaseAgreementType"]').change(function () {
            radioAgrrementToggle();
        });
    };

    VoaFor.agreementType = function () {

        function swapAgreementType() {
            if ($('#leaseAgreementType_leaseTenancy').is(':checked')) {
                $('.agreementType').text(VoaMessages.textLabel('labelSingleLeaseTenancy'));
            }

            if ($('#leaseAgreementType_licenceOther').is(':checked')) {
                $('.agreementType').text(VoaMessages.textLabel('labelLicenseWritten'));
            }
        }

        swapAgreementType();

        $('[name="leaseAgreementType"]').change(function () {
            swapAgreementType();
        });
    };

    VoaFor.populateLettingsAddress = function () {
        if (!$('#sublet_0_tenantAddress_buildingNameNumber_text').val() && $('#sublet_0_tenantAddress_buildingNameNumber_text').closest('div').hasClass('form-grouped-error')) {
            //do nothing
        } else if (!$('#sublet_0_tenantAddress_buildingNameNumber_text').val()) {
            $('#sublet_0_tenantAddress_buildingNameNumber_text').val($('#addressBuildingNameNumber').val());
            $('#sublet_0_tenantAddress_street1_text').val($('#addressStreet1').val());
            $('#sublet_0_tenantAddress_street2_text').val($('#addressStreet2').val());
        }
        if (!$('#sublet_0_tenantAddress_postcode_text').val() && $('#sublet_0_tenantAddress_postcode_text').closest('div').hasClass('form-grouped-error')) {
            //do nothing
        } else if (!$('#sublet_0_tenantAddress_postcode_text').val()) {
            $('#sublet_0_tenantAddress_postcode_text').val($('#addressPostcode').val());
        }
        $('.subletsHidden').remove();
    };

    VoaFor.getReferrer = function () {
        var s = window.location.href, i = s.indexOf('?') + 1, r, v;

        if (ref) {
            r = ref.replace(/\//g, '');
        }

        if (i > 0) {
            v = [s.slice(0, i), 'id=' + r + '&', s.slice(i)].join('');
        } else {
            v = s + '?id=' + r;
        }

        $('#footerBetaFeedback, #betaFeedback').each(function (e) {
            $(this).attr('href', $(this).attr('href') + '?page=' + window.location.href.substr(window.location.href.lastIndexOf('/') + 1).replace(/\?/g, '&'));
        });

        $('.form--feedback [name="referrer"], .form--feedback #referer').val(v);
    };

    VoaFor.formatPostcode = function () {
        $(document).on('change', '.postcode', function (e) {
            if ($(this).val()) {
                $(this).val($(this).val().replace(' ', '').replace(/(.{3})$/, ' $1').toUpperCase());
            }
        });
    };

    VoaFor.toggleAgentLeaseContainsRentReviews = function () {
        if ($('.form-error .leaseContainsRentReviews_agent') && $('#leaseContainsRentReviews .label-span').text() === VoaMessages.textLabel('labelLeaseContainsRentReviews')) {
            $('.form-error .leaseContainsRentReviews_agent').text(VoaMessages.textLabel('labelLeaseContainsRentReviews'));
        }

    };

    VoaFor.toggleImage = function () {
        if ($('html[lang="cy"]').length > 0 && $('.letter-img').length > 0) {
            $('html[lang="cy"] .letter-img').attr('src', $('.letter-img').attr('src').replace(/.png/g, '-cy.png'));
        }
    };

    VoaFor.toggleYearsMonths = function () {
        var yNumber = parseInt($('.yNumber').text(), 10);
        var mNumber = parseInt($('.mNumber').text(), 10);
        if (yNumber === 2) {
            $('.yText').text(VoaMessages.textLabel('labelYears1'));
        }
        if (yNumber === 3 || yNumber === 4 || yNumber === 6) {
            $('.yText').text(VoaMessages.textLabel('labelYears2'));
        }
        if (yNumber === 1 || yNumber === 5 || yNumber === 7 || yNumber === 8 || yNumber === 9 || yNumber === 10) {
            $('.yText').text(VoaMessages.textLabel('labelYears3'));
        }
        if (yNumber >= 11) {
            $('.yText').text(VoaMessages.textLabel('labelYears4'));
        }
        if (mNumber === 2) {
            $('.mText').text(VoaMessages.textLabel('labelMonths2'));
        } else {
            $('.mText').text(VoaMessages.textLabel('labelMonths1'));
        }
    };

    VoaFor.timeOutReminder = function () {
        var timeout = $('#signOutTimeout').val();
        var countdown = $('#signOutCountdown').val();
        var signOutUrl = $('#signOutUrl').val();

        if (window.GOVUK.timeoutDialog && signOutUrl) {
            window.GOVUK.timeoutDialog({
                timeout: timeout,
                countdown: countdown,
                keepAliveUrl: window.location,
                signOutUrl: signOutUrl
            });
        }
    };

})(jQuery);