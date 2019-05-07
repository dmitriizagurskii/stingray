/*!
 * Star Rating <LANG> Translations
 *
 * This file must be loaded after 'star-rating.js'. Patterns in braces '{}', or
 * any HTML markup tags in the messages must not be converted or translated.
 *
 * NOTE: this file must be saved in UTF-8 encoding.
 *
 * @see http://github.com/kartik-v/bootstrap-star-rating
 * @author Kartik Visweswaran <kartikv2@gmail.com>
 */
(function ($) {
    "use strict";
    $.fn.ratingLocales['<LANG>'] = {
        defaultCaption: '{rating} Stars',
        starCaptions: {
            0.5: 'Dreadful',
            1: 'Awful',
            1.5: 'Horrible',
            2: 'Very bad',
            2.5: 'Bad',
            3: 'OK',
            3.5: 'Good',
            4: 'Very good',
            4.5: 'Excellent',
            5: 'Outstanding'
        },
        clearButtonTitle: 'Clear',
        clearCaption: 'Not Rated'
    };
})(window.jQuery);
