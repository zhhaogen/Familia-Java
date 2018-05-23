#include "familia_java_util_CMT19937.h"
#include <random>
 std::mt19937 engine;

 JNIEXPORT void JNICALL Java_familia_java_util_CMT19937_setSeed
   (JNIEnv *env, jobject jobj, jlong sseq){
	 engine.seed(sseq);
 }

 JNIEXPORT jdouble JNICALL Java_familia_java_util_CMT19937_nextDouble
   (JNIEnv *env, jobject jobj){
	 double min = 0.0,  max = 1.0;
	  static std::uniform_real_distribution<double> distribution(min, max);
	  return distribution(engine);
 }
