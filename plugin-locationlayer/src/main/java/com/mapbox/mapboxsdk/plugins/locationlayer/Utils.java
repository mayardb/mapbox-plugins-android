package com.mapbox.mapboxsdk.plugins.locationlayer;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.mapbox.services.commons.geojson.Point;

final class Utils {

  private Utils() {
    // Class should not be initialized
  }

  /**
   * Util for finding the shortest path from the current icon rotated degree to the new degree.
   *
   * @param magneticHeading         the new position of the rotation
   * @param previousMagneticHeading the current position of the rotation
   * @return the shortest degree of rotation possible
   * @since 0.4.0
   */
  static float shortestRotation(float magneticHeading, float previousMagneticHeading) {
    double diff = previousMagneticHeading - magneticHeading;
    if (diff > 180.0f) {
      magneticHeading += 360.0f;
    } else if (diff < -180.0f) {
      magneticHeading -= 360.f;
    }
    return magneticHeading;
  }

  static Bitmap getBitmapFromDrawable(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else {
      // width and height are equal for all assets since they are ovals.
      Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bitmap;
    }
  }

  static Bitmap generateShadow(Drawable drawable, float elevation) {
    int widthHeight = toEven(10 * (elevation == 0 ? 1 : elevation));
    Bitmap bitmap = Bitmap.createBitmap(widthHeight, widthHeight,
      Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  static Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableRes,
                              @ColorInt Integer tintColor) {
    Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
    if (tintColor == null) {
      return drawable;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable.setTint(tintColor);
    } else {
      drawable.mutate().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
    }
    return drawable;
  }

  /**
   * Used for animating the user location icon
   *
   * @since 0.1.0
   */
  static class PointEvaluator implements TypeEvaluator<Point> {
    // Method is used to interpolate the user icon animation.
    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
      return Point.fromCoordinates(new double[] {
        startValue.getCoordinates().getLongitude() + (
          (endValue.getCoordinates().getLongitude() - startValue.getCoordinates().getLongitude()) * fraction),
        startValue.getCoordinates().getLatitude() + (
          (endValue.getCoordinates().getLatitude() - startValue.getCoordinates().getLatitude()) * fraction)
      });
    }
  }

  /**
   * Casts the value to an even integer.
   */
  private static int toEven(float value) {
    int i = (int) (value + .5f);
    if (i % 2 == 1) {
      return i - 1;
    }
    return i;
  }
}
