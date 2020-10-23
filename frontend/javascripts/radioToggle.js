(function ($) {
    'use strict';

    VoaRadioToggle.radioDataShowField = function () {
        function radioDataShowFieldElement(that) {
            var classname = $(that).attr('name').replace(/(:|\.|\[|\])/g,'\\$1');
            if ($(that).closest('[data-show-field="true"]').is(':checked')) {
                var element = '.' + classname + '';
                $(element).removeClass('hidden');

                //show full address fields
                if (VoaCommon.showAddressfieldsCondition(element) === true) {
                    VoaCommon.showAddressfields(element);
                }

                if ($('input:radio[name="' + $(that).attr('name') + '"]').is(':radio')) {
                    var checkedName = $('.' + $(that).attr('name') + ' input:radio').attr('name');
                    $('[name="' + checkedName + '"][checked]').prop('checked', true);
                }

                //show error if yes selected
                if($('#propertyIsSublet_true').is(':checked') || $('#propertyAlterations_false').is(':checked')){
                    $('.form-error').removeClass('hidden');
                }

            } else {

                if($(that).closest('fieldset').find('input:radio, input:checkbox').is('[data-show-fields]') ||
                    $(that).closest('fieldset').find('input:radio, input:checkbox').is('[data-show-field]')){
                    $('.' + classname + '').addClass('hidden');
                    //hide error if no selected
                    if($('#propertyIsSublet_false').is(':checked') || $('#propertyAlterations_false').is(':checked')){
                        $('.form-error').addClass('hidden');
                    }

                }

                $('.' + $(that).attr('name') + ' input:radio')
                    .prop('checked', false)
                    .closest('label')
                    .removeClass('selected');
            }

            if ($('[data-show-details="true"]').is(':checked')) {
                $('.data-show-details').removeClass('hidden');

            } else {
                $('.data-show-details').addClass('hidden');
            }

        }

        //data-show-field data attribute on load
        var selected = [];
        $('input:radio:checked').not('[name="overseas"]').each(function () {
            radioDataShowFieldElement(this);
            selected.push($(this).attr('name'));
            //toggle the add multi fields button visibility
            VoaFor.addMultiButtonState();
        });
        
        //data-show-field data attribute on change
        $(document).on('change', 'input:radio', function () {
            radioDataShowFieldElement(this);
            $('[name="' + $(this).attr('name') + '"]').removeAttr('checked');
            if (selected) {
                $(this).prop('checked', true);
            }
            $(this).attr('checked', 'checked');
            $.each(selected, function (index, value) {
                $('[name="' + value + '"][checked]').prop('checked', true);
            });
            //toggle the add multi fields button visibility
            VoaFor.addMultiButtonState();
        });

    };


    VoaRadioToggle.radioDataShowFields = function () {


        function radioDataShowFieldsElement(that) {
            var fieldsToShow = $(that).attr('data-show-fields').split(','),
                hiddenGroup = $(that).attr('name');

            //hide all inputs
            $('[data-show-fields-group="' + hiddenGroup + '"] input').closest('.form-group').addClass('hidden');
            if(that.hasAttribute('skipfieldset') === false) {
                $('[data-show-fields-group="' + hiddenGroup + '"] input').closest('fieldset').addClass('hidden');
            }
            $.each(fieldsToShow, function (index, value) {
                //only show selected fields with the data-show-fields data attribute
                var element = $('[data-show-fields-group="' + hiddenGroup + '"] [name="' + value + '"]');
                var elementFind = element.closest('.postcode-lookup-group');
                element.closest('.form-group').removeClass('hidden');
                element.closest('fieldset').removeClass('hidden');
                //show full address fields
                elementFind.find('.form-group-lookup').css('display', 'none');
                elementFind.find('.showHide-group').css('display', 'inline-block');
                elementFind.find('.showHide-group .form-group').removeClass('hidden');
                elementFind.find('.manual-address').text(VoaMessages.textLabel('findPostcode'));
            });
        }

        //data-show-fields data attribute on load
        $('[type="radio"][data-show-fields]:checked').each(function () {
            radioDataShowFieldsElement(this);
        });
        //data-show-fields data attribute on change
        $('[type="radio"][data-show-fields]').change(function () {
            radioDataShowFieldsElement(this);

            //make sure postcode lookup fields are open
            var hiddenGroup = $(this).attr('name');
            var element = $('[data-show-fields-group="' + hiddenGroup + '"] .postcode-lookup-group');
            element.find('.form-group-lookup').css('display', 'block');
            element.find('.showHide-group').css('display', 'none');
            element.find('.manual-address').text(VoaMessages.textLabel('enterManual'));
        });
    };

    VoaRadioToggle.toggleFieldsBasedOnCheckedRadioButton = function(){

        function showFieldsAssociatedWithSelectedRadiosOnPageLoad() {
            //when page loads show the fields associated with selected radio buttons if any are checked
            var $checkedRadioButtons = $('.radio-button-that-show-hides input[type=radio]:checked');
            if ($checkedRadioButtons && $checkedRadioButtons.length > 0) {
                showHideFieldsBasedOnRadioButtonValue();
            }
        }

        var showHideFieldsBasedOnRadioButtonValue = function () {
            var elementsToShowOrHide = $('[data-hidden-by]');
            if(elementsToShowOrHide.length > 0){
                elementsToShowOrHide.each(function(){
                    var elem = $(this);
                    var hiddenBy = elem.attr('data-hidden-by');
                    var showWhenEquals = elem.attr('data-show-when-value-equals');
                    var $elementToToggle = $(this)[0].hasAttribute('data-hides-this') ? $(this) : $(this).closest('.govuk-form-group');

                    var fieldsThatShowHideThis = hiddenBy.split('|');

                    if(fieldsThatShowHideThis.length > 1){
                        //multiple elements hide this so we need to find fields and values that show/hide
                        var valuesOfFieldsThatShowThis = showWhenEquals.split('|');
                        var show = false;
                        //we will loop through all the fields that show this and their values
                        //then we will see if any are matching a value that shows this, otherwise
                        //we will hide it.
                        for (var i = 0; i < fieldsThatShowHideThis.length; i++) {
                            //get the field name from the list of fields that show this
                            var curr = fieldsThatShowHideThis[i];
                            var $masterEl = $('input[name=' + curr + ']:checked');
                            //get the corresponding value that would show this
                            var valThatShows = valuesOfFieldsThatShowThis[i];
                            if (valThatShows === $masterEl.val()) {
                                //if any field should show this we show it, so break loop.
                                show = true;
                                break;
                            }
                        }
                        if(show){
                            $elementToToggle.removeClass('hidden');
                        } else {
                            $elementToToggle.addClass('hidden');
                        }
                    }else{
                        //a single element hides this
                        var $masterElem = $('input[name=\'' + hiddenBy + '\']:checked');
                        if (showWhenEquals.split('|').indexOf($masterElem.val()) > -1) {
                            $elementToToggle.removeClass('hidden');
                        } else {
                            $elementToToggle.addClass('hidden');
                        }
                    }
                });
            }
        };

        //add hidden class to all form groups containing data-hidden-by INPUT elements
        $('*[data-hidden-by]').closest('.govuk-form-group').addClass('hidden');
        //for form fields that need to be shown/hidden as a group.
        // This is necessary as some in the group are also toggled by items in group itself.
        //the data-hides-this attr is added to the div or fieldset or element that is going to be hidden.
        $('[data-hides-this][data-hidden-by]').addClass('hidden');
        //now run on page load to show any that should be shown based on which radios are already selected
        showFieldsAssociatedWithSelectedRadiosOnPageLoad();
        $(document).on('change', '.radio-button-that-show-hides input[type=radio]' , function () {
            showHideFieldsBasedOnRadioButtonValue();
        });
    };

})(jQuery);