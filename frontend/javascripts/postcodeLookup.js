(function ($) {
    'use strict';

    VoaPostCode.postcodeLookup = function () {
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

    VoaPostCode.postcodeLookupElements = function () {
        $('.postcode-lookup-group').each(function () {
            if ($(this).find('.showHide-group .form-group').hasClass('has-error') || VoaCommon.showAddressfieldsCondition(this) === true) {
                VoaCommon.showAddressfields(this);
            }
        });
    };

})(jQuery);