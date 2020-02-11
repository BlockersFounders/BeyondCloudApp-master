package edu.skku.edu.jungsunwoung.memorial_test;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;

/*이 java파일은 encoding 혹은 decoding을 위해서 struct처럼 구조체화 시키는 것(function 타입으로)입니다

 */

public class FunctionUtils {

    public static final String CONTRACT_ADDRESS = "0x1425cbbbfb6d3c309c81fc8193e2e38f94c333b7";
//블록을 생성하는 function과 연결
    public static Function createBLock(String name, String birth, String will) {
        return new Function("set"
                , Arrays.asList(
                new Utf8String(name)
                , new Utf8String(birth)
                , new Utf8String(will))
                , Collections.emptyList());
    }
    //블록에서 데이터를 가지고 오는 함수, int값을 받아서 가지고 온다
    public static Function callBlock(int key) {
        return new Function("get"
                , singletonList(new Uint(BigInteger.valueOf(key)))
                , Arrays.asList(
                new TypeReference<Utf8String>() {
                }
                , new TypeReference<Utf8String>() {
                }
                , new TypeReference<Utf8String>() {
                }


        ));
    }

    //블록 길이(데이터 길이)를 세는 함수
    public static Function countBlock() {
        return new Function("getdatasCount"
                , Collections.emptyList()
                , singletonList(new TypeReference<Uint>() {
        }));

    }
}