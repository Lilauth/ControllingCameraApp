package letseeitcrash.android.controllincameraapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link letseeitcrash.android.controllincameraapp.CameraFragment} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback{

    private OnCameraFragmentInteractionListener mListener;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback jpegCallback;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jpegCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // fos = null;
                //String filename;
                //String appDirectory;
                try {
                    //intenta guardar en el espacio de la aplicación
                  /*  ContextWrapper cw = new ContextWrapper(getContext());
                    File privateAppDirectory = cw.getDir("imageDir", Context.MODE_PRIVATE);*/

                    File publicImageDirectory = new File(String.format("/sdcard/"));
                    String filename = String.format("%d.jpeg", System.currentTimeMillis());
                    File file = new File(/*privateAppDirectory*/publicImageDirectory,filename);

                    Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ExifInterface exif=new ExifInterface(file.toString());

                    if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                        realImage= rotate(realImage, 90);
                    } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                        realImage= rotate(realImage, 270);
                    } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                        realImage= rotate(realImage, 180);
                    } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                        realImage= rotate(realImage, 90);
                    }

                    FileOutputStream fos =  new FileOutputStream(file);
                    boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                  //  fos.write(data);
                    fos.close();
                    Log.e("Info", bo + "");

                    if (mListener != null) {
                        mListener.onCameraFragmentInteraction(Uri.fromFile(file));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("error", "file not found");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("error", "IO exception");
                }
                refreshCamera();
            }
        };
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root_view = inflater.inflate(R.layout.fragment_camera, container, false);

        surfaceView = (SurfaceView) root_view.findViewById(R.id.surfaceView2);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button b_takePicture = (Button) root_view.findViewById(R.id.b_take_picture);
        b_takePicture.setOnClickListener(new SavePicture());

        return root_view;
    }

    public void captureImage2() throws IOException {
        camera.takePicture(null, null, jpegCallback);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCameraFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCameraFragmentInteractionListener) {
            mListener = (OnCameraFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            setCameraDisplayOrientation(0, camera);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        catch (IOException e1){
            System.err.println(e1);
            return;
        }
        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        //for debug only
        Camera.Parameters param;
        param = camera.getParameters();
        Log.e("param", param.flatten());
        //end of manual debug
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCameraFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCameraFragmentInteraction(Uri uri);
    }

    private class SavePicture implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                captureImage2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
