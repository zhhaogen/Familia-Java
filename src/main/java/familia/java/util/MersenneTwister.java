/*
 *  疑似乱数生成機  移植  
 * 
 *  Mersenne Twister with improved initialization (2002)
 *  http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/mt.html
 *  http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/mt19937ar.html
 */
// = 移植元ラインセンス =======================================================
// ======================================================================
/* 
   A C-program for MT19937, with initialization improved 2002/2/10.
   Coded by Takuji Nishimura and Makoto Matsumoto.
   This is a faster version by taking Shawn Cokus's optimization,
   Matthe Bellew's simplification, Isaku Wada's real version.
   Before using, initialize the state by using init_genrand(seed) 
   or init_by_array(init_key, key_length).
   Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,
   All rights reserved.                          
   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
     1. Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
     2. Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
     3. The names of its contributors may not be used to endorse or promote 
        products derived from this software without specific prior written 
        permission.
   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
   A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
   Any feedback is very welcome.
   http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html
   email: m-mat @ math.sci.hiroshima-u.ac.jp (remove space)
*/
// ======================================================================
package familia.java.util;

public class MersenneTwister {
	/* Period parameters */  
	private static final int N = 624;
	private static final int M = 397;
	private static final long MATRIX_A = 0x9908b0dfL;  /* constant vector a */
	private static final long UMASK = 0x80000000L; /* most significant w-r bits */
	private static final long LMASK = 0x7fffffffL; /* least significant r bits */
	
	private static final long mixBits(long u, long v) {
		return (u & MersenneTwister.UMASK) | (v & MersenneTwister.LMASK);
	}
	
	private static final long twist(long u, long v) {
		return (mixBits(u, v) >> 1) ^ ((v & 1) == 1 ? MersenneTwister.MATRIX_A : 0);
	}
	
	private long[] state; /* the array for the state vector  */
	private int left = 1;
	private int initf = 0;
	private int next = 0;
	private long seed;
	
	public MersenneTwister() {
		this(0);
	}
	
	public MersenneTwister(long seed) {
		state = new long[MersenneTwister.N];
	}
	
	 

	public long getSeed() {
		return seed;
	}

	/* initializes state[N] with a seed */
	public void setSeed(long seed) {
		this.seed = seed & 0xffffffffL;
		state[0] = seed & 0xffffffffL;
		for (int j = 1; j < MersenneTwister.N; j++) {
			state[j] = (1812433253L * (state[j - 1] ^ (state[j - 1] >> 30)) + j);
	        /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
	        /* In the previous versions, MSBs of the seed affect   */
	        /* only MSBs of the array state[].                        */
	        /* 2002/01/09 modified by Makoto Matsumoto             */
	        state[j] &= 0xffffffffL;  /* for >32 bit machines */
		}
		left  = 1; initf = 1;
	}

	/* initialize by an array with array-length */
	/* init_key is the array for initializing keys */
	/* key_length is its length */
	/* slight change for C++, 2004/2/26 */
	public void init_by_array(long[] init_key, int key_length) {
	    int i, j, k;
	    setSeed(19650218L);
	    i = 1; j = 0;
	    k = (MersenneTwister.N > key_length ? MersenneTwister.N : key_length);
	    for (; k > 0; k--) {
	        state[i] = (state[i] ^ ((state[i-1] ^ (state[i-1] >> 30)) * 1664525L))
	          + init_key[j] + j; /* non linear */
	        state[i] &= 0xffffffffL; /* for WORDSIZE > 32 machines */
	        i++; j++;
	        if (i >= MersenneTwister.N) { state[0] = state[MersenneTwister.N - 1]; i = 1; }
	        if (j >= key_length) j = 0;
	    }
	    for (k = MersenneTwister.N - 1; k > 0; k--) {
	        state[i] = (state[i] ^ ((state[i - 1] ^ (state[i - 1] >> 30)) * 1566083941L))
	          - i; /* non linear */
	        state[i] &= 0xffffffffL; /* for WORDSIZE > 32 machines */
	        i++;
	        if (i >= MersenneTwister.N) { state[0] = state[MersenneTwister.N - 1]; i = 1; }
	    }

	    state[0] = 0x80000000L; /* MSB is 1; assuring non-zero initial array */ 
	    left = 1; initf = 1;
	}

	public void next_state() {
	    int p = 0;
	    int j;

	    /* if init_genrand() has not been called, */
	    /* a default initial seed is used         */
	    if (initf == 0) setSeed(5489L);

	    left = MersenneTwister.N;
	    next = 0;
	    
	    for (j = MersenneTwister.N - MersenneTwister.M + 1; --j > 0; p++) 
	        state[p] = state[p + MersenneTwister.M] ^ twist(state[p], state[p + 1]);

	    for (j = MersenneTwister.M; --j > 0; p++) 
	        state[p] = state[p + MersenneTwister.M -MersenneTwister.N] ^ twist(state[p], state[p + 1]);

	    state[p] = state[p + MersenneTwister.M - MersenneTwister.N] ^ twist(state[p], state[0]);
	}

	/* generates a random number on [0,0xffffffff]-interval */
	public long genrand_int32() {
	    long y;

	    if (--left == 0) next_state();
	    y = state[next++];

	    /* Tempering */
	    y ^= (y >> 11);
	    y ^= (y << 7) & 0x9d2c5680L;
	    y ^= (y << 15) & 0xefc60000L;
	    y ^= (y >> 18);
	    
	    return y;
	}

	/* generates a random number on [0,0x7fffffff]-interval */
	public long genrand_int31() {
	    long y;

	    if (--left == 0) next_state();
	    y = state[next++];

	    /* Tempering */
	    y ^= (y >> 11);
	    y ^= (y << 7) & 0x9d2c5680L;
	    y ^= (y << 15) & 0xefc60000L;
	    y ^= (y >> 18);

	    return (long)(y>>1);
	}

	/* generates a random number on [0,1]-real-interval */
	public double genrand_real1() {
	    long y;

	    if (--left == 0) next_state();
	    y = state[next++];

	    /* Tempering */
	    y ^= (y >> 11);
	    y ^= (y << 7) & 0x9d2c5680L;
	    y ^= (y << 15) & 0xefc60000L;
	    y ^= (y >> 18);

	    return (double)y * (1.0/4294967295.0); 
	    /* divided by 2^32-1 */ 
	}

	/* generates a random number on [0,1)-real-interval */
	public double genrand_real2() {
	    long y;

	    if (--left == 0) next_state();
	    y = state[next++];

	    /* Tempering */
	    y ^= (y >> 11);
	    y ^= (y << 7) & 0x9d2c5680L;
	    y ^= (y << 15) & 0xefc60000L;
	    y ^= (y >> 18);

	    return (double)y * (1.0/4294967296.0); 
	    /* divided by 2^32 */
	}

	/* generates a random number on (0,1)-real-interval */
	public double genrand_real3() {
	    long y;

	    if (--left == 0) next_state();
	    y = state[next++];

	    /* Tempering */
	    y ^= (y >> 11);
	    y ^= (y << 7) & 0x9d2c5680L;
	    y ^= (y << 15) & 0xefc60000L;
	    y ^= (y >> 18);

	    return ((double)y + 0.5) * (1.0/4294967296.0); 
	    /* divided by 2^32 */
	}

	/* generates a random number on [0,1) with 53-bit resolution*/
	public double genrand_res53() { 
	    long a = genrand_int32() >> 5, b = genrand_int32() >> 6; 
	    return (a * 67108864.0 + b) * (1.0 / 9007199254740992.0); 
	} 
	/* These real versions are due to Isaku Wada, 2002/01/09 added */

	/**
	 * @return
	 */
	public double nextDouble()
	{
		genrand_real2();
		return genrand_real2();
	}
}