/* jshint -W117 */

function VoaCommon() {
}

function GOVUK() {
}

(function ($) {
    'use strict';
    $('body').ready(function () {
        VoaCommon.GdsSelectionButtons();
        VoaCommon.radioDataShowField();
        VoaCommon.radioDataShowFields();
        VoaCommon.postcodeLookupElements();
        VoaCommon.smoothScrollAndFocus();
        VoaCommon.addAnchors();
        VoaCommon.addErrorAnchors();
        VoaCommon.anchorFocus();
        VoaCommon.details();
        VoaCommon.characterCount();
        VoaCommon.stickyFooter();
        VoaCommon.toggleHelp();
    });

    VoaCommon.smoothScrollAndFocus = function () {
        $('.form-error a[href*=#]:not([href=#])').click(function (e) {
            e.preventDefault();
            var element = $(this).attr('href').replace('_anchor', '');
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                $('html,body').animate({
                    scrollTop: target.offset().top
                }, 600, function () {
                    if ($('div' + element + ' input').is('input:text') || $('div' + element + ' textarea').is('textarea')) {
                        $('div' + element + ' input, div' + element + ' textarea').focus();
                    } else {
                        $(this).blur();
                    }
                });
            }
        });
    };

    VoaCommon.getQueryString = function (name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)'),
            results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    VoaCommon.getSegment = function(index){
        var pathArray = window.location.pathname.split( '/' );
        return pathArray[index];
    };

    VoaCommon.addAnchors = function () {
        $('fieldset input, fieldset textarea').not('fieldset .multi-fields-group input, fieldset .multi-fields-group textarea').each(function () {
            var name = $(this).attr('name');
            var spanId = name.replace(/[_\[\].]/g, '_').replace('__', '_');
            $(this).closest('fieldset').prepend('<span id="' + spanId + '_anchor"></span>');
            $('span#' + spanId + '_anchor').not(':first').remove();
        });
    };

    VoaCommon.addErrorAnchors = function () {

        $('.form-error li a').each(function(){
            if($(''+$(this).attr('href')+'_anchor').length !== 0){
                $(this).attr('href', $(this).attr('href')+'_anchor');
            }
        });
    };

    VoaCommon.anchorFocus = function () {
        if (window.location.hash) {
            var input = $(window.location.hash);
            if (input.hasClass('form-group')) {
                setTimeout(function () {
                    if (input.find('input').is(':radio')) {
                        input.find('input:checked').focus();
                    } else {
                        var html = input.find('input, textarea').val();
                        input.find('input, textarea').focus().val('').val(html);
                        if (input.find('input').hasClass('typeahead')) {
                            input.find('input.typeahead').val(input.closest('.form-group').find('input.typeahead:first').attr('value'));
                        }
                    }
                }, 100);
            } else if (input.hasClass('multi-fields-group')) {
                setTimeout(function () {
                    var find = input.find('textarea:first'),
                        element = find.val();
                    find.focus().val('').val(element);
                }, 100);
            } else {
                setTimeout(function () {
                    var find = input.closest('.form-group').find('input:first'),
                        element = find.val();
                    find.focus().val('').val(element);
                }, 100);

            }
        }
    };

    VoaCommon.GdsSelectionButtons = function () {
        var $blockLabels = $('.block-label input[type="radio"], .block-label input[type="checkbox"]');
        new GOVUK.SelectionButtons($blockLabels);
    };

    VoaCommon.radioDataShowField = function () {
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

                if($(that).closest('fieldset').find('input:radio, input:checkbox').is('[data-show-fields]') || $(that).closest('fieldset').find('input:radio, input:checkbox').is('[data-show-field]')){
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
        });
        //data-show-field data attribute on change
        //$('input:radio').change(function () {

        $(document).on('change', 'input:radio', function () {
            //console.log('--- '+$(this).attr('name'));
            radioDataShowFieldElement(this);
            $('[name="' + $(this).attr('name') + '"]').removeAttr('checked');
            if (selected) {
                $(this).prop('checked', true);

            }
            //console.log(selected);
            $(this).attr('checked', 'checked');
            $.each(selected, function (index, value) {
                $('[name="' + value + '"][checked]').prop('checked', true);
            });

        });
    };


    VoaCommon.radioDataShowFields = function () {
        function radioDataShowFieldsElement(that) {
            var fieldsToShow = $(that).attr('data-show-fields').split(','),
                hiddenGroup = $(that).attr('name');

            //hide all inputs
            $('[data-show-fields-group="' + hiddenGroup + '"] input').closest('.form-group').addClass('hidden');
            $('[data-show-fields-group="' + hiddenGroup + '"] input').closest('fieldset').addClass('hidden');

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
                elementFind.find('.manual-address').text(VoaFor.textLabel('findPostcode'));
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
            element.find('.manual-address').text(VoaFor.textLabel('enterManual'));
        });
    };

    VoaCommon.postcodeLookupElements = function () {
        $('.postcode-lookup-group').each(function () {
            if ($(this).find('.showHide-group .form-group').hasClass('has-error') || VoaCommon.showAddressfieldsCondition(this) === true) {
                VoaCommon.showAddressfields(this);
            }
        });
    };

    VoaCommon.showAddressfieldsCondition = function (that) {
        if ($(that).find('.showHide-group .address-field-one input').val() !== '' ||
            $(that).find('.showHide-group .address-field-two input').val() !== '' ||
            $(that).find('.showHide-group .address-field-three input').val() !== '' ||
            $(that).find('.showHide-group .address-field-postcode input').val() !== '') {
            return true;
        } else {
            return false;
        }
    };

    VoaCommon.showAddressfields = function (that) {
        $(that).find('.showHide-group').css('display', 'inline-block');
        $(that).find('.showHide-group .form-group').removeClass('hidden');
        $(that).find('.form-group-lookup').css('display', 'none');
        $(that).find('.manual-address').text(VoaFor.textLabel('findPostcode'));
    };

    VoaCommon.details = function(){

        $('details').each(function(){
            $(this).find('summary span').after('<span class="screenDetails visuallyhidden">' + VoaFor.textLabel('labelReveal') + '</span>');
        });

        $('details').click(function(){
            if($(this).attr('open')){
                $(this).find('summary span.screenDetails').text(VoaFor.textLabel('labelReveal'));
            }else{
                $(this).find('summary span.screenDetails').text(VoaFor.textLabel('labelHide'));

            }

        });
    };

    VoaCommon.characterCount = function(){
        var maxLength = $('.charCount .chars').attr('data-max-length');

        function doMaxLength(that, e){
            var length = $(that).val().length;
            length = maxLength-length;
            $(that).closest('.form-group').find('.chars').text(length);
        }

        $('textarea').each(function(e){
            $(this).attr('maxlength', maxLength);
            doMaxLength(this, e);
        });

        $(document).on('keyup', 'textarea', function (e) {
            doMaxLength(this, e);
        });

        // < IE9
        $('textarea[maxlength]').keyup(function(){
            var text = $(this).val();
            var limit = $(this).attr('maxlength');
            if(text.length > limit){
                $(this).val(text.substr(0, limit));
            }
        });
    };


    VoaCommon.stickyFooter = function(){
        var footerBar = $('#footerBar');
        if(VoaCommon.getQueryString('edit')){
            footerBar.css({display:'block'});
            $(window).scroll(function(event) {
                var d = $(document).height(),
                w = $(window).height(),
                s = $(this).scrollTop(),
                bottomBound = 400;
                if(d - (w + s) < (bottomBound+100)) {
                    footerBar.css({ bottom: bottomBound - (d - (w + s)), display: 'none'});
                } else {
                    footerBar.css({ bottom: -30, display: 'inline-block'});
                }
            });
        }else{
            footerBar.css({display:'none'});
        }
    };

    VoaCommon.toggleHelp = function(){
        $('.form-help-toggle').click(function(e){
            e.preventDefault();
            $('#helpForm').toggle();
        });
    };



})(jQuery);
