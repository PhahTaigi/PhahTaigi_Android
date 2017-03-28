package com.taccotap.taigidictparser.utils;

import java.util.ArrayList;

public class LomajiPhraseSplitter {
    private static final String TAG = LomajiPhraseSplitter.class.getSimpleName();

    public class LomajiPhraseSplitterResult {
        private ArrayList<String> splitStrings = new ArrayList<>();
        private ArrayList<String> splitSperators = new ArrayList<>();


        public ArrayList<String> getSplitStrings() {
            return splitStrings;
        }

        public void setSplitStrings(ArrayList<String> splitStrings) {
            this.splitStrings.clear();
            this.splitStrings.addAll(splitStrings);
        }

        public ArrayList<String> getSplitSperators() {
            return splitSperators;
        }

        public void setSplitSperators(ArrayList<String> splitSperators) {
            this.splitSperators.clear();
            this.splitSperators.addAll(splitSperators);
        }
    }

    public LomajiPhraseSplitterResult split(String lomajiPhrase) {
        ArrayList<String> splitStrings = new ArrayList<>();
        ArrayList<String> splitSperators = new ArrayList<>();

        LomajiPhraseSplitterResult lomajiPhraseSplitterResult = new LomajiPhraseSplitterResult();

        int count = lomajiPhrase.length();
        int startIndex = 0;
        for (int i = 0; i < count; i++) {
            String charString = lomajiPhrase.substring(i, i + 1);
//            Log.d(TAG, "charString = " + charString);

            String splitSperator = null;

            if (charString.equals(" ")) {
                splitSperator = " ";
            } else if (charString.equals("-")) {
                if (i + 2 < count && lomajiPhrase.substring(i + 1, i + 2).equals("-")) {
                    splitSperator = "--";
                } else {
                    splitSperator = "-";
                }
            }

            if (splitSperator != null) {
                String splitString = lomajiPhrase.substring(startIndex, i);

                splitStrings.add(splitString);
                splitSperators.add(splitSperator);

                startIndex += splitString.length() + splitSperator.length();
//                Log.d(TAG, "startIndex = " + startIndex + ", splitString=" + splitString + ", splitSperator=\"" + splitSperator + "\"");

                if (splitSperator.length() > 1) {
                    i += (splitSperator.length() - 1);
                }
            } else {
                if (i == count - 1) {
                    String splitString = lomajiPhrase.substring(startIndex);
                    splitStrings.add(splitString);
                }
            }
        }

        lomajiPhraseSplitterResult.setSplitStrings(splitStrings);
        lomajiPhraseSplitterResult.setSplitSperators(splitSperators);

        return lomajiPhraseSplitterResult;
    }
}
