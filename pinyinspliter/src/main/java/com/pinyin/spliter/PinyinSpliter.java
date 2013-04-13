package com.pinyin.spliter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * split Chinese pinyin <p>
 * 
 */
public class PinyinSpliter
{
   
    public boolean isPinyinSequence(String keyword)
    {
        boolean isPinyin = true;
        List<String> splitKeywordList = splitPinyin(keyword);
        for (String token : splitKeywordList)
        {
            if(!PinyinDicUtil.isPinyin(token))
            {
                isPinyin = false;
                break;
            }
        }
        
        return isPinyin;
    }

    public List<String> splitPinyin(String pinyin)
    {
        List<String> pinyinList = new ArrayList<String>();
        
        if(pinyin != null && !pinyin.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            
            sb.append(pinyin.charAt(0));
            
            for (int i = 1; i < pinyin.length(); i++)
            {
                char c = pinyin.charAt(i);

                if(isPinyinChar(c) ^ isPinyinChar(sb.charAt(sb.length()-1)))
                {
                    String candidate = sb.toString();
                    
                    if(isPinyinChar(sb.charAt(sb.length()-1)))
                    {
                        Deque<String> splitPinyinStack = forwardSplitPinyin(candidate);
                        addToList(pinyinList, splitPinyinStack); 
                    }
                    else
                    {
                        pinyinList.add(candidate);
                    }                    
                    
                    sb.setLength(0);                    
                }
                
                sb.append(c);
            }
            
            if(sb.length() > 0)
            {
                String candidate = sb.toString();
                if(isPinyinChar(sb.charAt(sb.length()-1)))
                {
                    Deque<String> splitPinyinStack = forwardSplitPinyin(candidate);
                    addToList(pinyinList, splitPinyinStack);                    
                }
                else
                {
                    pinyinList.add(candidate);
                }
            }
        }       
        
        return pinyinList;
    }

    private void addToList(List<String> dest, Deque<String> src)
    {
        while(!src.isEmpty())
        {
            dest.add(src.pollLast());
        } 
    }
    
    public Deque<String> forwardSplitPinyin(String pinyin)
    {
        Deque<String> tokenStack = new LinkedList<String>();

        String current = pinyin;

        while(current.length() > 0)
        {
            String token = forwardFindMaxToken(current);
            
            //find a candidate pinyin
            if(token != null)
            {
                tokenStack.push(token);
                current = current.substring(token.length(), current.length());
                
                continue;
            }            
            
            //invalidate pinyin sequence
            if(current.length() == pinyin.length())
            {
                tokenStack.push(current);
                break;
            }
            
            //backtracking            
            while(!tokenStack.isEmpty())
            {
                String lastPinyin = tokenStack.pop();
                
                String maxBacktrackingToken = forwardFindMaxToken(lastPinyin.substring(0, lastPinyin.length()-1));
                
                if(maxBacktrackingToken != null)
                {
                    tokenStack.push(maxBacktrackingToken);                            
                    
                    current = lastPinyin.substring(maxBacktrackingToken.length()) + current;
                    
                    break;
                }
                else
                {
                    current = lastPinyin + current;
                }
            }                                
            
            if(tokenStack.isEmpty())
            {
                tokenStack.push(pinyin);
                
                break;
            }
            
        }           
        
        return tokenStack;
    }
    
    private String forwardFindMaxToken(String input)
    {
        String firstToken = null;
        
        for(int i=0;i<input.length();i++)
        {
            String candidateToken = input.substring(0, input.length()-i);
            if(PinyinDicUtil.isPinyin(candidateToken))
            {
                firstToken = candidateToken;
                
                break;
            }
        }
        
        return firstToken;
    }
    
    private boolean isPinyinChar(char ch)
    {
        boolean isPinyinChar = false;
        
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
        {
            isPinyinChar = true;
        }
        
        return isPinyinChar;
    }

    public String findMatchSnippet(List<String> queryPinyinList, String rawText)
    {
        StringBuilder matchSnippet = new StringBuilder();
        
        int position = 0;
        for (String pinyin : queryPinyinList)
        {
            int matchPosition = findSingleMatchToken(pinyin, rawText, position);
            
            //not match, will use original pinyin
            if(matchPosition < 0)
            {
                matchSnippet.append(pinyin);
                
                continue;
            }
            
            position = matchPosition + 1;
            
            matchSnippet.append(rawText.substring(matchPosition, matchPosition + 1));                                   
        }       
        
        return matchSnippet.toString();        
    }
    
    private int findSingleMatchToken(String pinyin, String shopName, int startPosition)
    {
        int matchPosition = -1;
        Set<String> chineseCharSet = PinyinDicUtil.getChineseCharSet(pinyin);
        for(int i = startPosition; i < shopName.length(); i++)
        {
            String chineseChar = shopName.substring(i, i + 1);
            if(chineseCharSet != null && chineseCharSet.contains(chineseChar))
            {
                matchPosition = i; 
                
                break;
            }
        }
        
        //if we can't find the match token with input sequence, try find it from the start point
        if(matchPosition < 0)
        {
            for(int i = 0; i < shopName.length(); i++)
            {
                String chineseChar = shopName.substring(i, i + 1);
                if(chineseCharSet != null && chineseCharSet.contains(chineseChar))
                {
                    matchPosition = i; 
                    
                    break;
                }
            }
        }
        
        return matchPosition;
    }
}
