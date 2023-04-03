public class AugmentedImageNode extends AnchorNode {
    private final AugmentedImage image;
    private final Context context;
    private final String caption;

    public AugmentedImageNode(Context context, AugmentedImage image, String caption) {
        this.context = context;
        this.image = image;
        this.caption = caption;

        ViewRenderable.builder()
                .setView(context, R.layout.ar_caption_layout)
                .build()
                .thenAccept(renderable -> {
                    ImageView imageView = renderable.getView().findViewById(R.id.ar_image_view);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ar_image_icon));
                    TextView textView = renderable.getView().findViewById(R.id.ar_caption_text_view);
                    textView.setText(caption);

                    setRenderable(renderable);
                });

        // code to add speech functionality to AR node
    }
}
