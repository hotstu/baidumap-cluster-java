package com.dzbkq.myapplication;/*
 * OpenSimplex Noise sample class.
 */

import org.junit.Test;

import java.io.IOException;

/**
 * OpenSimplex Noise is useful for basically all of the same things as Perlin Noise:
 * the noise takes an input point (in 2D, 3D, or 4D) and returns a value between -1 and 1.
 * The output values vary smoothy with the input coordinate changes.
 *
 */
public class OpenSimplexNoiseTest
{
	private static final int WIDTH = 512;
	private static final int HEIGHT = 512;
	private static final double FEATURE_SIZE = 24;

	@Test
	public  void test_Noise()
		throws IOException {
		
		OpenSimplexNoise noise = new OpenSimplexNoise();
		for (int y = 0; y < HEIGHT; y++)
		{
			for (int x = 0; x < WIDTH; x++)
			{
				double value = noise.eval(x / FEATURE_SIZE, y / FEATURE_SIZE, 0.0);
				System.out.println(value);
			}
		}
	}
}