(function ($) {
    'use strict';
    VoaFor.linkShowManualAddress = function () {
        $('.showHide').click(function (e) {
            e.preventDefault();
            var element = $(this).closest('.postcode-lookup-group');
            if (element.find('.showHide-group').is(':visible')) {
                element.find('.showHide-group').css('display', 'none');
                element.find('.form-group').addClass('hidden');
                element.find('.form-group-lookup').css('display', 'block');
                element.find('.form-group-lookup input:first').focus();
            } else {
                element.find('.showHide-group').css('display', 'block');
                element.find('.form-group').removeClass('hidden');
                element.find('.form-group-lookup').css('display', 'none');
                element.find('.showHide-group input:first').focus();
            }
            $(this).html(element.find('.showHide-group').is(':visible') ? VoaMessages.textLabel('findPostcode') : VoaMessages.textLabel('enterManual'));
        });
    };

    VoaFor.service = function () {
        return 'sending-rental-information';
    };

    
    VoaFor.postcodeLookup = function () {
        var addressData = [], line1, line2, line3, town, pcode;

        $('.findPostcode').click(function (e) {
            e.preventDefault();
            var postcode = $(this).closest('.form-group-lookup').find('.postcode').val().replace(/ /g, ''),
                url = '/' + VoaFor.service() + '/lookup?postcode=' + postcode + '',
                that = this,
                formGroupLookup = $(that).closest('.form-group-lookup'),
                postcodeLookupGroup = $(that).closest('.postcode-lookup-group'),
                loading = formGroupLookup.find('.loading');

            function space(i) {
                if (i === '') {
                    i = '';
                } else {
                    i = i + ' ';
                }
                return i;
            }

            function lookupError(message, element) {
                element.find('.error').remove();
                element.addClass('form-grouped-error').find('.postcode').before('<p class="error">' + message + '</p>');
                element.find('.postcode').focus();
            }

            if (postcode !== '') {

                formGroupLookup.find('.addressList option').not('option[value=""]').remove();

                $.ajax({
                    type: 'GET',
                    url: url,
                    beforeSend: function () {
                        loading.removeClass('hidden');
                    },
                    success: function (data) {
                        if (data.length > 0 ) {
                            addressData = data;
                            var address = space(line1) + space(line2) + space(line3);
                            var option = formGroupLookup.find('.addressList option:last');
                            $.each(data, function (i, item) {
                                line1 = item.address.lines[0],
                                line2 = item.address.lines[1],
                                line3 = item.address.lines[2];
                                if(!line1){ line1 = ''; }
                                if(!line2){ line2 = ''; }
                                if(!line3){ line3 = ''; }
                                town = item.address['town'],
                                pcode = item.address['postcode'],
                                address = space(line1) + space(line2) + space(line3),
                                option = formGroupLookup.find('.addressList option:last');
                                $('.addressList').append('<option value="' + i + '">' + address + '</option>');
                            });
                            formGroupLookup.find('.error').remove();
                            postcodeLookupGroup.find('.showHide-group').css('display', 'none');
                            postcodeLookupGroup.find('.postcode-results').css('display', 'block');
                            postcodeLookupGroup.find('.manual-address').css('display', 'table');
                            formGroupLookup.removeClass('form-grouped-error');
                            loading.addClass('hidden');

                            if (addressData.length === 1) {
                                postcodeLookupGroup.find('.addressList').change();
                            }
                        } else {
                            lookupError(VoaMessages.textLabel('errorPostcode'), formGroupLookup);
                            postcodeLookupGroup.find('.postcode-results').css('display', 'none');
                            loading.addClass('hidden');
                        }

                    },
                    error: function (error) {

                        lookupError(VoaMessages.textLabel('errorPostcode'), formGroupLookup);
                        postcodeLookupGroup.find('.postcode-results').css('display', 'none');
                        loading.addClass('hidden');
                    }

                });
            } else {
                lookupError(VoaMessages.textLabel('errorPostcode'), formGroupLookup);
            }
        });

        $('.postcode-results label').text(VoaMessages.textLabel('labelSelectAddress'));
        $('.postcode-results').css('margin-top', '30px');

        $('.addressList').change(function (e) {
            e.preventDefault();

            $('.showHide-group').css('display', 'block');
            $('.showHide-group input').closest('.form-group').removeClass('hidden');
            var element = $(this).closest('.postcode-lookup-group');
            var index;
            if (addressData.length === 1) {
                index = 0;
            } else {
                index = $(this).find('option:selected').index();
            }
            var lineOne = addressData[index]['address']['lines'][0];
            var lineTwo = addressData[index]['address']['lines'][1];
            var lineThree = addressData[index]['address']['lines'][2];
            var lineTown = addressData[index]['address']['town'];
            var linePostcode = addressData[index]['address']['postcode'];
            if(!lineOne){ lineOne = ''; }
            if(!lineTwo){ lineTwo = ''; }
            if(!lineThree){ lineThree = ''; }

            element.find('.showHide-group .address-field-one input').val(lineOne);

            if (!lineTwo) {
                element.find('.showHide-group .address-field-two input').val(lineTwo + lineTown);
                element.find('.showHide-group .address-field-three input').val(lineThree);
            } else if (!lineThree) {
                element.find('.showHide-group .address-field-two input').val(lineTwo);
                element.find('.showHide-group .address-field-three input').val(lineTown);
            } else if (!lineTwo && !lineThree) {
                element.find('.showHide-group .address-field-two input').val(lineTown);
            } else {
                element.find('.showHide-group .address-field-two input').val(lineTwo);
                element.find('.showHide-group .address-field-three input').val(lineThree + ', ' + lineTown);
            }

            element.find('.showHide-group .address-field-postcode input').val(linePostcode);
            element.find('.postcode-results, .form-group-lookup').css('display', 'none');
            element.find('.manual-address').text(VoaMessages.textLabel('findPostcode'));

            //remove field errors
            element.find('.showHide-group').each(function () {
                $(this).removeClass('has-error');
                $(this).find('.error').unwrap().remove();
            });

        });

        $('.manual-address').click(function () {
            $(this).blur();
            $('.addressAbroadDiv').addClass('hidden');
            $('.addressAbroadDiv input').val('');
            var element = $(this).closest('.postcode-lookup-group');
            element.find('.showHide-group input').val('');

            $('.address-abroad').removeClass('hidden');
            $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaMessages.textLabel('labelAddress'));
            $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaMessages.textLabel('labelPostcode'));
            $('#overseas_false').prop('checked', true);

            $('[name="overseas"]').closest('label').removeClass('selected');
            $('#overseas_false').closest('label').addClass('selected');

        });
    };

    VoaFor.addressAbroad = function () {
        if(!$('[name="overseas"]').is(':checked')){
            $('#overseas_false').prop('checked', true);
        }

        function toggleOverseasLink(){
            if($('#overseas_true').is(':checked')) {
                $('.address-abroad').addClass('hidden');
                $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaMessages.textLabel('labelOverseasAddress'));
                $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaMessages.textLabel('labelOverseasPostcode'));
            }else{
                $('.address-abroad').removeClass('hidden');
                $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaMessages.textLabel('labelAddress'));
                $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaMessages.textLabel('labelPostcode'));
            }
        }
        toggleOverseasLink();

        $('.address-abroad').click(function (e) {
            e.preventDefault();
            $(this).addClass('hidden');
            $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaMessages.textLabel('labelOverseasAddress'));
            $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaMessages.textLabel('labelOverseasPostcode'));
            $('#overseas_true').prop('checked', true);
            $('[name="overseas"]').closest('label').removeClass('selected');
            $('#overseas_true').closest('label').addClass('selected');
        });

        $('[name="overseas"]').change(function(){
            toggleOverseasLink();
        });
    };

    VoaFor.errorFocus = function () {
        if ($('.form-error')) {
            $('.form-error').focus();
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

    VoaFor.changeIds = function(that,i){
        var index;
        $(that).find('input, textarea').not('[type="hidden"]').each(function () {
            index = i;
            var attrFormgroupId = $(this).closest('.form-group').attr('id'),
            attrFor = $(this).closest('.form-group').find('label').attr('for'),
            attrId = $(this).attr('id'),
            attrName = $(this).attr('name'),
            s = $(this).closest('.multi-fields-group').attr('id'),
            o = s.substring(0, s.lastIndexOf('_') + 1),
            st = $(this).closest('.multi-fields-group').find('.form-date-dayMonth').attr('id'),
            st2 = $(this).closest('.multi-fields-group').find('[data-intel]').attr('class');
            $(this).closest('.form-group').attr('id', attrFormgroupId.replace(/(\d+)/g, index));
            $(this).closest('.form-group').find('label').attr('for', attrFor.replace(/(\d+)/g, index));
            $(this).attr('id', attrId.replace(/(\d+)/g, index));
            if(attrName.indexOf('street1') > -1 || attrName.indexOf('street2') > -1){
                $(this).attr('name', attrName.replace(/\[(\d+)\]/g, '['+index+']'));
            }else{
                $(this).attr('name', attrName.replace(/(\d+)/g, index));
            }
            $(this).closest('.multi-fields-group').attr('id', o+index);
            if(st){
                $(this).closest('.multi-fields-group').find('.form-date-dayMonth').attr('id', st.replace(/(\d+)/g, index));
            }
            if(st2){
                $(this).closest('.multi-fields-group').find('[data-intel]').attr('class', st2.replace(/(\d+)/g, index));
            }
        });
    };

    VoaFor.addFieldMulti = function () {
        if ($('.multi-fields-group').length >= $('fieldset[data-limit]').attr('data-limit')) {
            $('.add-multi-fields').hide();
        } else {
            $('.add-multi-fields').show();
        }
        $(document).on('click', '.add-multi-fields', function (e) {
            e.preventDefault();
            var limit = parseInt($(this).closest('fieldset').attr('data-limit'), 10);
            var element = $(this).closest('fieldset');
            element.find('.multi-fields-group:last').clone().insertAfter(element.find('.multi-fields-group:last'));
            element.find('.multi-fields-group:last p').remove();
            element.find('.multi-fields-group:last input').val('');
            element.find('.multi-fields-group:last .form-date-dayMonth, .multi-fields-group:last .form-group div').removeClass('form-grouped-error');
            element.find('.multi-fields-group').each(function (i) {
                VoaFor.changeIds(this,i);
            });
            element.find('.multi-fields-group:last .chars').text(element.find('.multi-fields-group:last .chars').attr('data-max-length'));
            $('.remove-multi-fields').css('display', 'inline-block');
            element.find('.multi-fields-group:last textarea').val('');
            element.find('.multi-fields-group:last .form-group:first input:eq(0), .multi-fields-group:last .form-group:first textarea:eq(0)').focus();
            element.find('.multi-fields-group:last').removeClass('form-grouped-error');
            element.find('.multi-fields-group:last').find('.form-group').removeClass('has-error');
            element.find('.multi-fields-group:last .intel-alert').addClass('hidden');
            element.find('.multi-fields-group:last .vat-alert').remove();
            VoaCommon.addAnchors();
            if ($('.multi-fields-group').length === limit) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    };

    VoaFor.removeFieldMulti = function () {
        $(document).on('click', '.remove-multi-fields', function (e) {
            e.preventDefault();
            var limit = parseInt($(this).closest('fieldset').attr('data-limit'), 10);
            $(this).closest('.multi-fields-group').remove();
            if ($('.multi-fields-group').length === 1) {
                $('.multi-fields-group:first').find('.remove-multi-fields').css('display', 'none');
            }
            if ($('.multi-fields-group').length < limit) {
                $('.add-multi-fields').show();
            } else {
                $('.add-multi-fields').hide();
            }
            $('.multi-fields-group').each(function (i) {
                VoaFor.changeIds(this,i);
            });
        });
        if ($('.multi-fields-group').length > 1) {
            $('.multi-fields-group').find('.remove-multi-fields').css('display', 'inline-block');
        }
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
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'annual'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelAnnual'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'quarterly'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelQuarterly'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'monthly'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelMonthly'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'weekly'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelWeekly'));
                }
            }
            element.change(function () {
                if($(this).val() === 'annual'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelAnnual'));
                }
                if($(this).val() === 'quarterly'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelQuarterly'));
                }
                if($(this).val() === 'monthly'){
                    $('.' + spanName + '').text(VoaMessages.textLabel('labelMonthly'));
                }
                if($(this).val() === 'weekly'){
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

    VoaFor.radioAgreement = function(){
        function radioAgrrementToggle(){
            if(!$('[name="leaseAgreementType"]').is(':checked')){
                //do nothing
            }else if($('#leaseAgreementType_verbal').is(':checked')){
                $('.leaseAgreementVerbal').removeClass('hidden');
                $('.leaseAgreementWritten').addClass('hidden');
            }else{
                $('.leaseAgreementVerbal').addClass('hidden');
                $('.leaseAgreementWritten').removeClass('hidden');
            }
        }
        radioAgrrementToggle();
        $('[name="leaseAgreementType"]').change(function(){
            radioAgrrementToggle();
        });
    };

    VoaFor.intelAlert = function () {

        //Section 5
        function intelCheckSection5(that){
            var landlordName, data;
            if($('[name="landlordFullName"]').val()){
                landlordName = $('[name="landlordFullName"]').val().split(' ').pop().toLowerCase();
            }
            var landlordAddress = $('[name="landlordAddress.buildingNameNumber"]').val()+$('[name="landlordAddress.street1"]').val()+$('[name="landlordAddress.street2"]').val()+$('[name="landlordAddress.postcode"]').val();
            if(landlordAddress){
                data = landlordName+landlordAddress.replace(/ /g,'').toLowerCase();
            }
            if($('#landlordConnectType_noConnected').is(':checked')) {
                if( data === $(that).attr('data-intel')){
                    $('.landlord-connect-type.intel-alert').removeClass('hidden');
                }else{
                    $('.landlord-connect-type.intel-alert').addClass('hidden');
                }
            }else{
                $('.landlord-connect-type.intel-alert').addClass('hidden');
            }
        }

        intelCheckSection5($('input#landlordConnectType_noConnected'));

        $('[name="landlordConnectType"]').change(function(){
            intelCheckSection5(this);
        });
        $('[name="landlordFullName"], [name="landlordAddress.buildingNameNumber"], [name="landlordAddress.street1"], [name="landlordAddress.street2"], [name="landlordAddress.postcode"]').change(function(){
            intelCheckSection5($('input#landlordConnectType_noConnected'));
        });

        //Section 9
        function intelCheckSection9(element){

            function intelCheckDate(element){
                var date1, date2;
                if($('[data-intel]').attr('data-intel')){
                    if($('#'+element+' input:eq(0)').val() && $('#'+element+' input:eq(1)').val()  && $('#'+element+' input:eq(2)').val() ){
                        date1 = $('#'+element+' input:eq(1)').val()+'/'+$('#'+element+' input:eq(0)').val()+'/'+$('#'+element+' input:eq(2)').val();
                        date2 = $('[data-intel]').attr('data-intel').split('/')[0].replace(/\s/g, '')+'/01/'+$('[data-intel]').attr('data-intel').split('/')[1].replace(/\s/g, '');
                    }
                }
                if (new Date(date1).getTime() < new Date(date2).getTime()) {
                    $('.'+element+'.intel-alert').removeClass('hidden');
                }else{
                    $('.'+element+'.intel-alert').addClass('hidden');
                }
            }
            function intelCheckDateChange(element){
                $('#'+element+' input').change(function(){
                    intelCheckDate(element);
                });
            }
            intelCheckDate(element);
            intelCheckDateChange(element);
        }

        function intelCheck(element){
            function intelCheckDate(element){
                var date1, date2;
                if($('[data-intel]').attr('data-intel')){
                    if($('#'+element+' input:eq(0)').val() && $('#'+element+' input:eq(1)').val()){
                        date1 = $('#'+element+' input:eq(0)').val()+'/01/'+$('#'+element+' input:eq(1)').val();
                        date2 = $('[data-intel]').attr('data-intel').split('/')[0].replace(/\s/g, '')+'/01/'+$('[data-intel]').attr('data-intel').split('/')[1].replace(/\s/g, '');
                    }
                }
                if (new Date(date1).getTime() < new Date(date2).getTime()) {
                    $('.'+element+'.intel-alert').removeClass('hidden');
                }else{
                    $('.'+element+'.intel-alert').addClass ('hidden');
                }
            }
            function intelCheckDateChange(element){
                $(document).on('change', '#'+element+' input', function (e) {
                    intelCheckDate(element);
                });
            }
            intelCheckDate(element);
            intelCheckDateChange(element);
        }

        //Section 9
        intelCheckSection9('rentBecomePayable');
        //Section 3
        intelCheck('firstOccupationDate');
        //Section 7
        intelCheck('rentReviewDetails_lastReviewDate');
        //Section 4
        intelCheck('sublet_rentFixedDate');
        //Section 10
        intelCheck('parking_annualSeparateParkingDate');
        //Section 13
        intelCheck('propertyAlterationsDetails_0_date');
        intelCheck('propertyAlterationsDetails_1_date');
        intelCheck('propertyAlterationsDetails_2_date');
        intelCheck('propertyAlterationsDetails_3_date');
        intelCheck('propertyAlterationsDetails_4_date');
        intelCheck('propertyAlterationsDetails_5_date');
        intelCheck('propertyAlterationsDetails_6_date');
        intelCheck('propertyAlterationsDetails_7_date');
        intelCheck('propertyAlterationsDetails_8_date');
        intelCheck('propertyAlterationsDetails_9_date');
    };

    VoaFor.excludeVat = function(){
        $(document).on('change', '.excludeVat', function () {
            var element = $(this).closest('.form-group');
            if(!element.find('.vat-alert').length){
                element.next('.vat-alert').remove();
                element.after('<div class="panel-indent panel-indent-info vat-alert">'+VoaMessages.textLabel('labelExcludeVat')+'</div>');
            } else{
                element.find('.vat-alert').remove();
            }
        });

    };

    VoaFor.agreementType = function(){

        function swapAgreementType(){
            if($('#leaseAgreementType_leaseTenancy').is(':checked')){
                //console.log('tenancy');
                $('.agreementType').text(VoaMessages.textLabel('labelSingleLeaseTenancy'));
            }

            if($('#leaseAgreementType_licenceOther').is(':checked')){
                //console.log('licenceOther');
                $('.agreementType').text(VoaMessages.textLabel('labelLicenseWritten'));
            }
        }

        swapAgreementType();

        $('[name="leaseAgreementType"]').change(function(){
            swapAgreementType();
        });
    };

    VoaFor.populateLettingsAddress = function(){
        if(!$('#sublet_0_tenantAddress_buildingNameNumber_text').val() && $('#sublet_0_tenantAddress_buildingNameNumber_text').closest('div').hasClass('form-grouped-error')){
            //do nothing
        }else if(!$('#sublet_0_tenantAddress_buildingNameNumber_text').val()){
            $('#sublet_0_tenantAddress_buildingNameNumber_text').val($('#addressBuildingNameNumber').val());
            $('#sublet_0_tenantAddress_street1_text').val($('#addressStreet1').val());
            $('#sublet_0_tenantAddress_street2_text').val($('#addressStreet2').val());
        }
        if(!$('#sublet_0_tenantAddress_postcode_text').val() && $('#sublet_0_tenantAddress_postcode_text').closest('div').hasClass('form-grouped-error')){
            //do nothing
        }else if(!$('#sublet_0_tenantAddress_postcode_text').val()){
            $('#sublet_0_tenantAddress_postcode_text').val($('#addressPostcode').val());
        }
        $('.subletsHidden').remove();
    };

    VoaFor.getReferrer = function(){
        var s = window.location.href, i = s.indexOf('?')+1, r, v;

        if(ref){
            r = ref.replace(/\//g, '');
        }

        if(i > 0){
            v = [s.slice(0, i), 'id='+r+'&', s.slice(i)].join('');
        }else{
            v  = s+'?id='+r;
        }
        
        $('#footerBetaFeedback, #betaFeedback').each(function(e){
            $(this).attr('href', $(this).attr('href')+'?page='+window.location.href.substr(window.location.href.lastIndexOf('/') + 1).replace(/\?/g, '&'));
        });

        $('.form--feedback [name="referrer"], .form--feedback #referer').val(v);
    };

    VoaFor.formatPostcode = function(){
        $(document).on('change', '.postcode', function (e) {
            if($(this).val()){
                $(this).val($(this).val().replace(' ', '').replace(/(.{3})$/, ' $1').toUpperCase());
            }
        });
    };

    VoaFor.toggleAgentLeaseContainsRentReviews = function(){
        if($('.form-error .leaseContainsRentReviews_agent') && $('#leaseContainsRentReviews .label-span').text() === VoaMessages.textLabel('labelLeaseContainsRentReviews')){
            $('.form-error .leaseContainsRentReviews_agent').text(VoaMessages.textLabel('labelLeaseContainsRentReviews'));
        }
        
    };

    VoaFor.toggleImage = function(){
        if($('html[lang="cy"]').length > 0 && $('.letter-img').length > 0){
            $('html[lang="cy"] .letter-img').attr('src', $('.letter-img').attr('src').replace(/.png/g, '-cy.png'));
        }
    };

    VoaFor.toggleYearsMonths = function(){
        var yNumber = parseInt($('.yNumber').text(), 10);
        var mNumber = parseInt($('.mNumber').text(), 10);
        if(yNumber === 2 ){
            $('.yText').text(VoaMessages.textLabel('labelYears1'));
        }
        if(yNumber === 3 || yNumber === 4 || yNumber === 6 ){
            $('.yText').text(VoaMessages.textLabel('labelYears2'));
        }
        if(yNumber === 1 || yNumber === 5 || yNumber === 7 || yNumber === 8 || yNumber === 9 || yNumber === 10){
            $('.yText').text(VoaMessages.textLabel('labelYears3'));
        }
        if(yNumber >= 11){
            $('.yText').text(VoaMessages.textLabel('labelYears4'));
        }
        if(mNumber === 2 ){
            $('.mText').text(VoaMessages.textLabel('labelMonths2'));
        }else{
            $('.mText').text(VoaMessages.textLabel('labelMonths1'));
        }
    };
})(jQuery);