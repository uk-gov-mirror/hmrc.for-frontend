(function ($) {
    
    VoaFeedback.feedbackOverrides = function(){
        //remove if js
        $('.deleteifjs').remove();
        //feedback overrides
        var isPreGds = ($('body.pre-gds-body').length === 1);
        if(isPreGds){
            $('.label--inlineRadio--overhead').addClass('block-label').removeClass('label--inlineRadio--overhead');
            $('.input--fullwidth').addClass('form-control').removeClass('input--fullwidth');
            $('#feedback-form fieldset fieldset legend').text(VoaMessages.textLabel('labelBetaFeedback'));
            $('#feedback-form fieldset small').text(VoaMessages.textLabel('labelBetaFeedback'));
            $('.form--feedback fieldset small').text(VoaMessages.textLabel('labelCommentLimit'));
            $('#feedback-form [type="submit"]').text(VoaMessages.textLabel('labelBetaFeedbackButton'));
            $('.form--feedback label[for="report-name"], .form--feedback label[for="feedback-name"] span:not(".form-field--error")').text(VoaMessages.textLabel('labelHelpName'));
            $('.form--feedback label[for="report-email"], .form--feedback label[for="feedback-email"] span:not(".form-field--error")').text(VoaMessages.textLabel('labelHelpEmail'));
            $('.form--feedback label[for="report-action"]').text(VoaMessages.textLabel('labelHelpAction'));
            $('.form--feedback label[for="report-error"]').text(VoaMessages.textLabel('labelHelpError'));
            $('.form--feedback input[id="report-name"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpName'));
            $('.form--feedback input[id="report-email"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpEmail'));
            $('.form--feedback input[id="report-action"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpAction'));
            $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpError'));
            $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpError'));
            $('.form--feedback button[type="submit"]').text(VoaMessages.textLabel('buttonHelpSend'));
            $('.form--feedback label[for="feedback-name"] .error-notification').text(VoaMessages.textLabel('errorHelpName'));
            $('.form--feedback label[for="feedback-email"] .error-notification').text(VoaMessages.textLabel('errorHelpEmailInvalid'));
            $('.form--feedback label[for="feedback-comments"] .error-notification').text(VoaMessages.textLabel('errorHelpCommentsRequired'));
            $('.form--feedback fieldset.form-field--error span.error-notification').text(VoaMessages.textLabel('errorHelpRequired'));

            //removed email and name from form, comments optional and bigger
            var comments = $('#feedback-form [for="feedback-comments"]').html();
            if (comments) {
                $('#feedback-form [for="feedback-comments"]').html(comments.replace('Comments', 'Comments (optional)'));
            }

            $('#feedback-form label[for="feedback-email"]').closest('div').hide();
            $('#feedback-form label[for="feedback-name"]').remove();
            $('#feedback-form label[for="feedback-email"]').remove();
            $('#feedback-form [name="feedback-name"]').val('Anonymous user');
            $('#feedback-form [name="feedback-email"]').val('anonymous@anonymous.com');
            $('#feedback-form [name="feedback-name"]').attr('type', 'hidden');
            $('#feedback-form [name="feedback-email"]').attr('type', 'hidden');
            //
            var feedbackAction = $('#feedback-form').attr('action');
            $('#feedback-form').attr('action', '/sending-rental-information' + feedbackAction);

        }
        //vacate form
        $('.vacated-form [for="report-name"]').text(VoaMessages.textLabel('vacateFormName'));
        $('.vacated-form [for="report-email"]').text(VoaMessages.textLabel('vacateFormEmail'));
        $('.vacated-form [for="report-error"]').text(VoaMessages.textLabel('vacateFormGiveDetails'));

        $('.vacated-form [name="report-action"]').closest('div').show();
        $('.vacated-form label[for="report-action"]').addClass('visuallyhidden');
        $('.vacated-form [name="report-action"]').val(VoaMessages.textLabel('labelWhatWereYouDoing')).attr('hidden', '');
        

        var needle  =  $('.form--feedback label[for="feedback-comments"]').html();
        if(needle){
            $('.form--feedback label[for="feedback-comments"]').html(needle.replace(/Comments/g, VoaMessages.textLabel('labelFeedbackComments')));

        }
    };

    VoaFeedback.toggleHelp = function(){
        $('.form-help-toggle').click(function(e){
            e.preventDefault();
            $('#helpForm').toggle();
        });
    };

    VoaFeedback.helpForm = function() {
        $('#helpForm').on('submit', 'form', function(event) {
            event.preventDefault();
            $('#report-submit').prop('disabled', true);
            $.ajax({
                type: 'POST',
                url:  $(this).attr('action'),
                data: VoaFeedback.prepareData($(this)),
                success: function(msg) {
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

    VoaFeedback.prepareData = function (_data) {
        var data = _data.serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});

        var referenceNumber = $('div.for-id').attr('for-id');
        if(referenceNumber === undefined) {
            referenceNumber = 'not provided';
        }

        var replaceRegex = new RegExp('https?://');

        data['report-error'] = data['report-error'] + '\n\nRef : ' + referenceNumber + '\n' +
            (String(window.location.href).replace(replaceRegex, ''));
        data['referer'] = String(window.location.href);
        data['isJavascript'] = true;

        return data;
    };


})(jQuery);