package com.moutamid.egyptiankeyboard.keyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Filter;
import android.widget.Filterable;

import com.fxn.stash.Stash;
import com.moutamid.egyptiankeyboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, Filterable {
    private static final String TAG = "SoftKeyboardd";
    static final boolean DEBUG = false;

    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;

    public InputMethodManager mInputMethodManager;

    public LatinKeyboardView mInputView;
    public static CandidateView mCandidateView;
    public CompletionInfo[] mCompletions;

    public StringBuilder mComposing = new StringBuilder();
    public boolean mPredictionOn;
    public boolean mCompletionOn;
    public int mLastDisplayWidth;
    public static boolean mCapsLock;
    public long mLastShiftTime;
    public long mMetaState;

    public LatinKeyboard mSymbolsKeyboard;
    public LatinKeyboard mSymbolsShiftedKeyboard;
    public LatinKeyboard mQwertyKeyboard;

    public LatinKeyboard mCurKeyboard;

    public String mWordSeparators;

    ArrayList<String> dictionary = new ArrayList();
    ArrayList<String> dictionaryAll = new ArrayList();

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
//        if (isNetworkConnected(getApplicationContext())) {
//            checkApp();
//        } else {
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
//        }
        // Constants.checkApp((Activity) getApplicationContext());
        Log.d("Checking123", "Started ");
    }

    private void setSuggestionsList() {
        Log.d(TAG, "setSuggestionsList: ");
        BufferedReader reader = null;
        try {
            Log.d(TAG, "setSuggestionsList: try {");
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("words_alpha.txt")));

            // do reading, usually loop until end of file reading
            String mLine;

            while ((mLine = reader.readLine()) != null) {
                //process line
                dictionary.add(mLine);
            }
            dictionary = new ArrayList<>(new LinkedHashSet<>(dictionary));
            //setSuggestions(list, false, false);
        } catch (IOException e) {
            Log.d(TAG, "setSuggestionsList: Exception: " + e);
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        Log.d(TAG, "onInitializeInterface: ");
        if (mQwertyKeyboard != null) {
            Log.d(TAG, "onInitializeInterface: if (mQwertyKeyboard != null) {");
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
    }

    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        Log.d(TAG, "onComputeInsets: ");
        if (!isFullscreenMode()) {
            Log.d(TAG, "onComputeInsets: if (!isFullscreenMode()) {");
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */


    @Override
    public View onCreateInputView() {
        Log.d(TAG, "onCreateInputView: ");
        // Constants.checkApp((Activity) getApplicationContext());
        String color = Stash.getString("color", "red");
        // mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input, null, false);
        if (color.equals("red")) {
            Log.d(TAG, "onCreateInputView: red");
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input, null, false);
        } else if (color.equals("blue")) {
            Log.d(TAG, "onCreateInputView: blue");
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.blue, null, false);
        } else if (color.equals("green")) {
            Log.d(TAG, "onCreateInputView: green");
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.green, null, false);
        }

        mInputView.setOnKeyboardActionListener(this);

        new Handler().postDelayed(() -> setLatinKeyboard(mQwertyKeyboard), 500);

        // setCandidatesViewShown(true);
        Log.d("Checking123", "Created ");
        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        Log.d(TAG, "setLatinKeyboard: ");
        boolean shouldSupportLanguageSwitchKey = false;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            shouldSupportLanguageSwitchKey = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
//        }
        // nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
        //setCandidatesViewShown(true);
        setSuggestionsList();
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidatesView: ");
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }


    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        Log.d(TAG, "onStartInput: ");
        setCandidatesViewShown(true);

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateCandidates();

        if (!restarting) {
            Log.d(TAG, "onStartInput: if (!restarting) {");
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard;
                mPredictionOn = true;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                    Log.d(TAG, "onStartInput: variation == ");
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
                Log.d(TAG, "onStartInput: default:");
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();
        Log.d(TAG, "onFinishInput: ");

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            Log.d(TAG, "onFinishInput: if (mInputView != null) {");
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        Log.d(TAG, "onStartInputView: ");

        // Apply the selected keyboard to the input view.
        setInputView(onCreateInputView());
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
        if (mInputMethodManager != null) {
            final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
            mInputView.setSubtypeOnSpaceKey(subtype);
        }
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
        Log.d(TAG, "onCurrentInputMethodSubtypeChanged: ");
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        Log.d(TAG, "onUpdateSelection: ");
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            Log.d(TAG, "onUpdateSelection: if condition");
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                Log.d(TAG, "onUpdateSelection: if (ic != null) {");
                ic.finishComposingText();
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        Log.d("Checking123", "Ok Working");
        Log.d(TAG, "onDisplayCompletions: ");
        if (mCompletionOn) {
            Log.d(TAG, "onDisplayCompletions: ");
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) {
                    stringList.add(ci.getText().toString());
                    //Toast.makeText(this, ""+ci.getText().toString(), Toast.LENGTH_SHORT).show();
                    getFilter().filter(ci.getText());
                    /*dictionaryAll.clear();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dictionaryAll = (ArrayList<String>) dictionary.stream().filter(word -> word.contains(ci.getText().toString())).collect(Collectors.toList());
                    }
                    setSuggestions(dictionaryAll, true, true);*/
                    // new FilterDictionary().execute(ci.getText().toString());
                }
            }

        }
        Log.d(TAG, "onDisplayCompletions: ended");
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "translateKeyDown: ");
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: ");
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "onKeyDown: case KeyEvent.KEYCODE_BACK:");
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                Log.d(TAG, "onKeyDown: case KeyEvent.KEYCODE_DEL:");
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                Log.d(TAG, "onKeyDown: case KeyEvent.KEYCODE_ENTER:");
                // Let the underlying text editor always handle these.
                return false;

            default:
                Log.d(TAG, "onKeyDown: default:");
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: ");
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        Log.d(TAG, "commitTyped: ");
        if (mComposing.length() > 0) {
            inputConnection.commitText((mComposing + " "), mComposing.length());
            mComposing.setLength(0);
//            updateCandidates();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        Log.d(TAG, "updateShiftKeyState: ");
        if (attr != null
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
            Log.d(TAG, "updateShiftKeyState: ");
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        Log.d(TAG, "isAlphabet: ");
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        Log.d(TAG, "keyDownUp: ");
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        Log.d(TAG, "sendKey: " + keyCode);
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
//                if (keyCode >= '0' && keyCode <= '9') {
//                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
//                } else {

                switch (keyCode) {
                    case 1:
                        commit("\uD80C\uDFFA");//𓏺
                        break;
                    case 2:
                        commit("\uD80C\uDFFB");//𓏻
                        break;
                    case 3:
                        commit("\uD80C\uDFFC");//𓏼
                        break;
                    case 4:
                        commit("\uD80C\uDFFD"); // 𓏽
                        break;
                    case 5:
                        commit("\uD80C\uDFFE"); // 𓏾
                        break;
                    case 6:
                        commit("\uD80C\uDFFF"); // 𓏿
                        break;
                    case 7:
                        commit("\uD80D\uDC00"); // 𓐀
                        break;
                    case 8:
                        commit("\uD80D\uDC01"); // 𓐁
                        break;
                    case 9:
                        commit("\uD80D\uDC02"); // 𓐂
                        break;
                    case 11:
                        commit("\uD80C\uDC0E"); // 𓈎
                        break;
                    case 12:
                        commit("\uD80C\uDC71"); // 𓅱
                        break;
                    case 13:
                        commit("\uD80C\uDC0D"); // 𓂝
                        break;
                    case 14:
                        commit("\uD80C\uDC0B"); // 𓂋
                        break;
                    case 15:
                        commit("\uD80C\uDCCF"); // 𓏏
                        break;
                    case 16:
                        commit("\uD80C\uDC8C"); // 𓇌
                        break;
                    case 17:
                        commit("\uD80C\uDFAF"); // 𓏲
                        break;
                    case 18:
                        commit("\uD80C\uDC8C"); // 𓇌
                        break;
                    case 19:
                        commit("\uD80C\uDFAF"); // 𓏯
                        break;
                    case 20:
                        commit("\uD80C\uDCAA"); // 𓊪
                        break;
                    case 21:
                        commit("\uD80C\uDC3F"); // 𓄿
                        break;
                    case 22:
                        commit("\uD80C\uDCB4"); // 𓋴
                        break;
                    case 23:
                        commit("\uD80C\uDC27"); // 𓂧
                        break;
                    case 24:
                        commit("\uD80C\uDC51"); // 𓆑
                        break;
                    case 25:
                        commit("\uD80C\uDCF9"); // 𓎼
                        break;
                    case 26:
                        commit("\uD80C\uDC14"); // 𓉔
                        break;
                    case 27:
                        commit("\uD80C\uDC53"); // 𓆓
                        break;
                    case 28:
                        commit("\uD80C\uDC61"); // 𓎡
                        break;
                    case 29:
                        commit("\uD80C\uDCAD"); // 𓃭
                        break;
                    case 30:
                        commit("\uD80C\uDC21"); // 𓈡
                        break;
                    case 31:
                        commit("\uD80C\uDC83"); // 𓊃
                        break;
                    case 32:
                        commit("\uD80C\uDCB7"); // 𓆷
                        break;
                    case 33:
                        commit("\uD80D\uDC0D"); // 𓐍
                        break;
                    case 34:
                        commit("\uD80C\uDDBB"); // 𓎛
                        break;
                    case 35:
                        commit("\uD80C\uDC80"); // 𓃀
                        break;
                    case 36:
                        commit("\uD80C\uDC16"); // 𓈖
                        break;
                    case 37:
                        commit("\uD80C\uDC53"); // 𓅓
                        break;
                    case 38:
                        commit("\uD80C\uDD7F"); // 𓍿
                        break;
                    case 39:
                        // KEYBOARD CHANGE
                        mInputMethodManager.showInputMethodPicker();
                        break;
                    case 40:
                        commit(" "); // SPACE BAR
                        break;
                    case 41:
                        commit("."); // DOT
                        break;
                    case 42:
                        // FACEBOOK
                        Uri uri = Uri.parse("http://www.google.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;


                }
//                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
//                }
                break;
        }
    }

    private void commit(String value) {
        getCurrentInputConnection().commitText(value, 1);
    }

    // Implementation of KeyboardViewListener

    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d(TAG, "onKey: ");
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
            return;
        }

//        if (isWordSeparator(primaryCode)) {
        // Handle separator
//            if (mComposing.length() > 0) {
//                commitTyped(getCurrentInputConnection());
//            }
        sendKey(primaryCode);
//            updateShiftKeyState(getCurrentInputEditorInfo());
        /*if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or somethin'
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                setLatinKeyboard(mQwertyKeyboard);
            } else {
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
        } else {
            handleCharacter(primaryCode, keyCodes);
        }*/
    }

    public void onText(CharSequence text) {
        Log.d(TAG, "onText: ");
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        Log.d(TAG, "updateCandidates: ");
        if (!mCompletionOn) {
            Log.d(TAG, "updateCandidates: if (!mCompletionOn) {");
            if (mComposing.length() > 0) {
                Log.d(TAG, "updateCandidates: if (mComposing.length() > 0) {");
                ArrayList<String> list = new ArrayList<>();
                list.add(mComposing.toString());
                getFilter().filter(mComposing.toString());

                /*dictionaryAll.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dictionaryAll = (ArrayList<String>) dictionary.stream().filter(word -> word.contains(mComposing.toString())).collect(Collectors.toList());
                }
                setSuggestions(dictionaryAll, true, true);*/
                // new FilterDictionary().execute(String.valueOf(mComposing));
            } else {
                Log.d(TAG, "updateCandidates: } else {");
                setCandidatesViewShown(false);
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        Log.d(TAG, "setSuggestions: ");
        if (mCandidateView != null) {
            Log.d(TAG, "setSuggestions: if (mCandidateView != null) {");
//            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
//            if (suggestions != null && suggestions.size() > 0) {
//                setCandidatesViewShown(true);
//            } else if (isExtractViewShown()) {
            setCandidatesViewShown(false);
//            }
        }

    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (isAlphabet(primaryCode) && mPredictionOn) {
            mComposing.append((char) primaryCode);
            Log.d(TAG, "handleCharacter: isAlphabet");
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidates();
        } else {
            Log.d(TAG, "handleCharacter: } else {");
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        Log.d("Checking123", "Picked index " + index);
        String s = dictionaryAll.get(index);
        Log.d("Checking123", "Picked " + s);

        if (!mCompletionOn && index >= 0) {
            if (index == 0) {
                getCurrentInputConnection().commitText((s + " "), 1);
            } else {
                getCurrentInputConnection().commitText((s + " "), index);
            }

            if (mCandidateView != null) {
                setCandidatesViewShown(false);
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here. But for this sample,
            // we will just commit the current text.
            commitTyped(getCurrentInputConnection());
        }
    }

    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }

    Filter filter = new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<String> filterList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filterList.addAll(dictionary);
            } else {
                for (String listModel : dictionary) {
                    if (listModel.startsWith(charSequence.toString().toLowerCase())) {
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        //run on Ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dictionaryAll.clear();
            dictionaryAll.addAll((Collection<? extends String>) filterResults.values);
            setSuggestions(dictionaryAll, true, true);
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class FilterDictionary extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setCandidatesViewShown(true);
            //setSuggestions(dictionary, true, true);
        }

        @Override
        protected String doInBackground(String... strings) {
            ArrayList<String> filterList = new ArrayList<>();
            if (strings.length <= 0) {
                filterList.addAll(dictionary);
            } else {
                for (String listModel : dictionary) {
                    if (listModel.toLowerCase().contains(strings[0])) {
                        filterList.add(listModel);
                    }
                }
            }
            dictionaryAll.clear();
            dictionary.addAll(filterList);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setSuggestions(dictionaryAll, true, true);
        }
    }


    public void checkApp() {
        String appName = "MyKeyboard"; //TODO: CHANGE APP NAME

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");
                Log.d("VALUE 123", "" + value);
                if (!value) {
                    mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    mWordSeparators = getResources().getString(R.string.word_separators);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		/*if (mConnectManager != null) {
			NetworkInfo[] mNetworkInfo = mConnectManager.getAllNetworkInfo();
			for (int i = 0; i < mNetworkInfo.length; i++) {
				if (mNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					return true;
			}
		}
		return false;*/

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return connected;
    }

}


