/* jshint -W117 */
/* jshint -W009 */
/* jshint -W098 */
function VoaFor() {
}
var ref;

(function ($) {
    'use strict';
    $('body').ready(function () {
        VoaFor.miscFun();
        VoaFor.errorFocus();
        VoaFor.linkShowManualAddress();
        VoaFor.postcodeLookup();
        VoaFor.addressAbroad();
        VoaFor.addField();
        VoaFor.removeField();
        VoaFor.addFieldMulti();
        VoaFor.removeFieldMulti();
        VoaFor.selectMobile();
        VoaFor.rentLength();
        VoaFor.updateLabelToggle();
        VoaFor.isEdit();
        VoaFor.radioAgreement();
        VoaFor.intelAlert();
        VoaFor.excludeVat();
        VoaFor.helpForm();
        VoaFor.agreementType();
        VoaFor.populateLettingsAddress();
        VoaFor.getReferrer();
        VoaFor.formatPostcode();
        VoaFor.toggleAgentLeaseContainsRentReviews();
        VoaFor.toggleImage();
        VoaFor.toggleYearsMonths();
    });

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
            $(this).html(element.find('.showHide-group').is(':visible') ? VoaFor.textLabel('findPostcode') : VoaFor.textLabel('enterManual'));
        });
    };

    VoaFor.service = function () {
        return 'sending-rental-information';
    };

    VoaFor.textLabel = function (t) {
        var text;
        if($('html').attr('lang') === 'en'){
            text = {
                findPostcode: 'Find the address by postcode',
                enterManual: 'Enter address manually',
                buttonUpdate: 'Update',
                buttonContinue: 'Continue',
                labelReveal: 'Press to reveal answer',
                labelHide: 'Press to hide answer',
                labelAddress: 'Address',
                labelOverseasAddress: 'Overseas address',
                labelPostcode: 'Postcode',
                labelOverseasPostcode: 'Zipcode/Postcode',
                labelSelectAddress: 'Select address',
                labelExcludeVat:'Please ensure this excludes VAT',
                labelBetaFeedback: 'How satisfied are you with this service?',
                labelBetaFeedbackImprove: 'How can we improve this form?',
                labelBetaFeedbackDontInclude: 'Don’t include any personal or financial information (such as your rent details).',
                labelBetaFeedbackButton: 'Send feedback',
                labelBetaFeedback5: 'Very satisfied',
                labelBetaFeedback4: 'Satisfied',
                labelBetaFeedback3: 'Neither satisfied or dissatisfied',
                labelBetaFeedback2: 'Dissatisfied',
                labelBetaFeedback1: 'Very dissatisfied',
                labelCommentLimit: 'Limit is 2000 characters',
                labelSingleLeaseTenancy: 'single lease or tenancy',
                labelLicenseWritten: 'licence or written',
                errorPostcode: 'Enter a valid UK postcode',
                labelLeaseContainsRentReviews: 'Does your client\'s licence or written agreement include any rent reviews?',
                labelHelpName: 'Name',
                labelHelpEmail: 'Email',
                labelHelpAction: 'What were you doing?',
                labelHelpError: 'What do you need help with?',
                errorHelpName: 'Please provide your name',
                errorHelpEmail: 'Please provide your email address.',
                errorHelpAction: 'Please enter details of what you were doing.',
                errorHelpError: 'Please enter details of what went wrong.',
                buttonHelpSend: 'Send',
                labelFeedbackComments: 'Comments',
                errorHelpEmailInvalid: 'Enter a valid email address.',
                errorHelpCommentsRequired: 'This field is required',
                errorHelpRequired: 'Tell us what you think of the service.',
                labelAnnual: 'Annual',
                labelQuarterly: 'Quarterly',
                labelMonthly: 'Monthly',
                labelWeekly: 'Weekly',
                labelYears1: 'years',
                labelYears2: 'years',
                labelYears3: 'years',
                labelYears4: 'years',
                labelMonths1: 'months',
                labelMonths2: 'months'

            };
        }else{
            text = {
                findPostcode: 'Dod o hyd i\'r cyfeiriad drwy god post',
                enterManual: 'Rhoi\'r cyfeiriad â llaw',
                buttonUpdate: 'Diweddaru',
                buttonContinue: 'Parhau',
                labelReveal: 'Gwasgwch i ddatgelu ateb',
                labelHide: 'Gwasgwch i guddio ateb',
                labelAddress: 'Cyfeiriad',
                labelOverseasAddress: 'Cyfeiriad dramor',
                labelPostcode: 'Cod post',
                labelOverseasPostcode: 'Cod Post',
                labelSelectAddress: 'Dewiswch gyfeiriad',
                labelExcludeVat: 'Sicrhewch nad yw hyn yn cynnwys TAW',
                labelBetaFeedback: 'Pa mor fodlon ydych chi ar y gwasanaeth?',
                labelBetaFeedbackImprove: 'Sut y gallwn wella\'r ffurflen hon?',
                labelBetaFeedbackDontInclude: 'Peidiwch â chynnwys unrhyw wybodaeth bersonol na gwybodaeth ariannol (fel eich manylion rhent).',
                labelBetaFeedbackButton: 'Anfon adborth',
                labelBetaFeedback5: 'Bodlon iawn',
                labelBetaFeedback4: 'Bodlon',
                labelBetaFeedback3: 'Ddim yn fodlon nac yn anfodlon',
                labelBetaFeedback2: 'Anfodlon',
                labelBetaFeedback1: 'Anfodlon iawn',
                labelCommentLimit: 'Yr uchafswm yw 2000 o nodau',
                labelSingleLeaseTenancy: 'prydles sengl neu gytundeb tenantiaeth',
                labelLicenseWritten: 'trwydded neu gytundeb ysgrifenedig',
                errorPostcode: 'Rhowch god post DU dilys',
                labelLeaseContainsRentReviews: 'A yw trwydded neu gytundeb ysgrifenedig eich cleient yn cynnwys unrhyw adolygiadau rhent?',
                labelHelpName: 'Enw',
                labelHelpEmail: 'E-bost',
                labelHelpAction: 'Beth roeddech chi\'n ei wneud?',
                labelHelpError: 'Gyda beth y mae angen help arnoch?',
                errorHelpName: 'Rhowch eich enw.',
                errorHelpEmail: 'Rhowch eich cyfeiriad e-bost.',
                errorHelpAction: 'Rhowch fanylion ynglŷn â beth roeddech chi\'n ei wneud.',
                errorHelpError: 'Rhowch fanylion ynglŷn â beth aeth o\'i le.',
                buttonHelpSend: 'Anfon',
                labelFeedbackComments: 'Sylwadau',
                errorHelpEmailInvalid: 'Rhowch gyfeiriad e-bost dilys.',
                errorHelpCommentsRequired: 'Mae\'r maes hwn yn ofynnol',
                errorHelpRequired: 'Dywedwch wrthym beth yw eich barn am y gwasanaeth.',
                labelAnnual: 'blynyddol',
                labelQuarterly: 'chwarterol',
                labelMonthly: 'misol',
                labelWeekly: 'wythnosol',
                labelYears1: 'flynedd',
                labelYears2: 'blynedd',
                labelYears3: 'mlynedd',
                labelYears4: 'o flynyddoedd',
                labelMonths1: 'mis',
                labelMonths2: 'fis'
            };
        }
        return text[t];
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
                            lookupError(VoaFor.textLabel('errorPostcode'), formGroupLookup);
                            postcodeLookupGroup.find('.postcode-results').css('display', 'none');
                            loading.addClass('hidden');
                        }

                    },
                    error: function (error) {

                        lookupError(VoaFor.textLabel('errorPostcode'), formGroupLookup);
                        postcodeLookupGroup.find('.postcode-results').css('display', 'none');
                        loading.addClass('hidden');
                    }

                });
            } else {
                lookupError(VoaFor.textLabel('errorPostcode'), formGroupLookup);
            }
        });

        $('.postcode-results label').text(VoaFor.textLabel('labelSelectAddress'));
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
            element.find('.manual-address').text(VoaFor.textLabel('findPostcode'));

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
            $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaFor.textLabel('labelAddress'));
            $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaFor.textLabel('labelPostcode'));
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
                $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaFor.textLabel('labelOverseasAddress'));
                $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaFor.textLabel('labelOverseasPostcode'));
            }else{
                $('.address-abroad').removeClass('hidden');
                $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaFor.textLabel('labelAddress'));
                $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaFor.textLabel('labelPostcode'));
            }
        }
        toggleOverseasLink();

        $('.address-abroad').click(function (e) {
            e.preventDefault();
            $(this).addClass('hidden');
            $('label[for="landlordAddress_buildingNameNumber_text"] .label-span').text(VoaFor.textLabel('labelOverseasAddress'));
            $('label[for="landlordAddress_postcode_text"] .label-span').text(VoaFor.textLabel('labelOverseasPostcode'));
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
                    $('.' + spanName + '').text(VoaFor.textLabel('labelAnnual'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'quarterly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelQuarterly'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'monthly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelMonthly'));
                }
                if($('[name="totalRent.rentLengthType"]:checked').val() === 'weekly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelWeekly'));
                }
            }
            element.change(function () {
                if($(this).val() === 'annual'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelAnnual'));
                }
                if($(this).val() === 'quarterly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelQuarterly'));
                }
                if($(this).val() === 'monthly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelMonthly'));
                }
                if($(this).val() === 'weekly'){
                    $('.' + spanName + '').text(VoaFor.textLabel('labelWeekly'));
                }
            });
        }

        setLengthLabel('totalRent.rentLengthType', 'totalRent-length');
    };

    VoaFor.updateLabelToggle = function () {
        function changeButtonLabel() {
            if ($('.section1 #isAddressCorrect_false').is(':checked') || VoaCommon.getQueryString('edit')) {
                $('.section1 button#continue').text(VoaFor.textLabel('buttonUpdate'));
            } else {
                $('.section1 button#continue').text(VoaFor.textLabel('buttonContinue'));
            }
        }

        $('.section1 [name="isAddressCorrect"]').change(function () {
            changeButtonLabel();
        });
        changeButtonLabel();
    };

    VoaFor.isEdit = function () {
        if (VoaCommon.getQueryString('edit')) {
            $('[name="continue_button"]').text(VoaFor.textLabel('buttonUpdate')).attr('name', 'update_button');
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
                element.after('<div class="panel-indent panel-indent-info vat-alert">'+VoaFor.textLabel('labelExcludeVat')+'</div>');
            } else{
                element.find('.vat-alert').remove();
            }
        });

    };

    VoaFor.miscFun = function(){
        //remove if js
        $('.deleteifjs').remove();
        //feedback overrides
        $('.label--inlineRadio--overhead').addClass('block-label').removeClass('label--inlineRadio--overhead');
        $('.input--fullwidth').addClass('form-control').removeClass('input--fullwidth');
        $('#feedback-form fieldset fieldset legend').text(VoaFor.textLabel('labelBetaFeedback'));
        $('#feedback-form fieldset small').text(VoaFor.textLabel('labelBetaFeedback'));
        $('.form--feedback fieldset small').text(VoaFor.textLabel('labelCommentLimit'));
        $('<strong class="feedbackLabel">'+VoaFor.textLabel('labelBetaFeedbackImprove')+'</strong>').insertBefore($('label[for="feedback-name"]'));
        $('<p class="feedbackLabelFooter">'+VoaFor.textLabel('labelBetaFeedbackDontInclude')+'</p>').insertAfter($('[for="feedback-comments"]'));
        $('<p class="feedbackLabelFooter">'+VoaFor.textLabel('labelBetaFeedbackDontInclude')+'</p>').insertAfter($('[for="contact-comments"]'));
        $('#feedback-form [type="submit"]').text(VoaFor.textLabel('labelBetaFeedbackButton'));
        $('[for="feedback-rating-5"]').html(VoaFor.textLabel('labelBetaFeedback5')+'<input type="radio" id="feedback-rating-5" name="feedback-rating" value="5">');
        $('[for="feedback-rating-4"]').html(VoaFor.textLabel('labelBetaFeedback4')+'<input type="radio" id="feedback-rating-4" name="feedback-rating" value="4">');
        $('[for="feedback-rating-3"]').html(VoaFor.textLabel('labelBetaFeedback3')+'<input type="radio" id="feedback-rating-3" name="feedback-rating" value="3">');
        $('[for="feedback-rating-2"]').html(VoaFor.textLabel('labelBetaFeedback2')+'<input type="radio" id="feedback-rating-2" name="feedback-rating" value="2">');
        $('[for="feedback-rating-1"]').html(VoaFor.textLabel('labelBetaFeedback1')+'<input type="radio" id="feedback-rating-1" name="feedback-rating" value="1">');
        
        $('.form--feedback label[for="report-name"], .form--feedback label[for="feedback-name"] span:not(".form-field--error")').text(VoaFor.textLabel('labelHelpName'));
        $('.form--feedback label[for="report-email"], .form--feedback label[for="feedback-email"] span:not(".form-field--error")').text(VoaFor.textLabel('labelHelpEmail'));
        $('.form--feedback label[for="report-action"]').text(VoaFor.textLabel('labelHelpAction'));
        $('.form--feedback label[for="report-error"]').text(VoaFor.textLabel('labelHelpError'));
        $('.form--feedback input[id="report-name"]').attr('data-msg-required', VoaFor.textLabel('errorHelpName'));
        $('.form--feedback input[id="report-email"]').attr('data-msg-required', VoaFor.textLabel('errorHelpEmail'));
        $('.form--feedback input[id="report-action"]').attr('data-msg-required', VoaFor.textLabel('errorHelpAction'));
        $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaFor.textLabel('errorHelpError'));
        $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaFor.textLabel('errorHelpError'));
        $('.form--feedback button[type="submit"]').text(VoaFor.textLabel('buttonHelpSend'));

        $('.form--feedback label[for="feedback-name"] .error-notification').text(VoaFor.textLabel('errorHelpName'));
        $('.form--feedback label[for="feedback-email"] .error-notification').text(VoaFor.textLabel('errorHelpEmailInvalid'));
        $('.form--feedback label[for="feedback-comments"] .error-notification').text(VoaFor.textLabel('errorHelpCommentsRequired'));
        $('.form--feedback fieldset.form-field--error span.error-notification').text(VoaFor.textLabel('errorHelpRequired'));

        var needle  =  $('.form--feedback label[for="feedback-comments"]').html();
        if(needle){
            $('.form--feedback label[for="feedback-comments"]').html(needle.replace(/Comments/g, VoaFor.textLabel('labelFeedbackComments')));

        }
    };

    VoaFor.helpForm = function() {
        $('#helpForm').on('submit', 'form', function(event) {
            event.preventDefault();
            $.ajax({
                type: 'POST',
                url:  $(this).attr('action'),
                data: $(this).serialize(),
                success: function(msg) {
                    console.log('Got success response: ' + msg);
                    $('.contact-form-copy').addClass('hidden');
                    $('.feedback-thankyou').removeClass('hidden');
                    $('.form--feedback').addClass('hidden');
                    $('#helpFormWrapper').addClass('hidden');
                },
                error: function(error) {
                    $('#helpFormWrapper').html('<p>'+error.responseText+'</p>');
                    $('.contact-form-copy').addClass('hidden');
                    $('.feedback-thankyou').removeClass('hidden');
                    $('.form--feedback').addClass('hidden');
                }
            });

        });
    };

    VoaFor.agreementType = function(){

        function swapAgreementType(){
            if($('#leaseAgreementType_leaseTenancy').is(':checked')){
                //console.log('tenancy');
                $('.agreementType').text(VoaFor.textLabel('labelSingleLeaseTenancy'));
            }

            if($('#leaseAgreementType_licenceOther').is(':checked')){
                //console.log('licenceOther');
                $('.agreementType').text(VoaFor.textLabel('labelLicenseWritten'));
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
        if($('.form-error .leaseContainsRentReviews_agent') && $('#leaseContainsRentReviews .label-span').text() === VoaFor.textLabel('labelLeaseContainsRentReviews')){
            $('.form-error .leaseContainsRentReviews_agent').text(VoaFor.textLabel('labelLeaseContainsRentReviews'));
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
            $('.yText').text(VoaFor.textLabel('labelYears1'));
        }
        if(yNumber === 3 || yNumber === 4 || yNumber === 6 ){
            $('.yText').text(VoaFor.textLabel('labelYears2'));
        }
        if(yNumber === 1 || yNumber === 5 || yNumber === 7 || yNumber === 8 || yNumber === 9 || yNumber === 10){
            $('.yText').text(VoaFor.textLabel('labelYears3'));
        }
        if(yNumber >= 11){
            $('.yText').text(VoaFor.textLabel('labelYears4'));
        }
        if(mNumber === 2 ){
            $('.mText').text(VoaFor.textLabel('labelMonths2'));
        }else{
            $('.mText').text(VoaFor.textLabel('labelMonths1'));
        }
    };

})(jQuery);
