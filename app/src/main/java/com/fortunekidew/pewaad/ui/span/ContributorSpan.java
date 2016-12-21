package com.fortunekidew.pewaad.ui.span;

/**
 * A span for marking up a dribbble player
 */
//public class ContributorSpan extends TouchableUrlSpan {
//
//    private final String playerName;
//    private final long playerId;
//    private final String playerUsername;
//
//    public ContributorSpan(String url,
//                           String playerName,
//                           long playerId,
//                           @Nullable String playerUsername,
//                           ColorStateList textColor,
//                           int pressedBackgroundColor) {
//        super(url, textColor, pressedBackgroundColor);
//        this.playerName = playerName;
//        this.playerId = playerId;
//        this.playerUsername = playerUsername;
//    }
//
//    @Override
//    public void onClick(View view) {
//        Intent intent = new Intent(view.getContext(), PlayerActivity.class);
//        intent.putExtra(PlayerActivity.EXTRA_PLAYER_NAME, playerName);
//        if (playerId > 0l) {
//            intent.putExtra(PlayerActivity.EXTRA_PLAYER_ID, playerId);
//        }
//        if (!TextUtils.isEmpty(playerUsername)) {
//            intent.putExtra(PlayerActivity.EXTRA_PLAYER_USERNAME, playerUsername);
//        }
//        view.getContext().startActivity(intent);
//    }
//}
