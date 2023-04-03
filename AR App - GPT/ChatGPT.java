import com.openai.api.ApiException;
import com.openai.api.models.Completion;
import com.openai.api.models.Engine;
import com.openai.api.models.ListEnginesResponse;
import com.openai.api.models.Model;
import com.openai.api.models.Parameter;
import com.openai.api.models.TextCompletionRequest;
import com.openai.api.services.CompletionsService;
import com.openai.api.services.EnginesService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatGPT {
    private final String apiKey;

    public ChatGPT(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generateText(String prompt) throws IOException, ApiException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EnginesService enginesService = retrofit.create(EnginesService.class);

        Call<ListEnginesResponse> enginesCall = enginesService.listEngines(apiKey);
        ListEnginesResponse enginesResponse = enginesCall.execute().body();
        List<Engine> engines = enginesResponse.getData();

        String engineId = "";
        for (Engine engine : engines) {
            if (engine.getId().equals("text-davinci-002")) {
                engineId = engine.getId();
                break;
            }
        }

        Model model = new Model();
        model.setId(engineId);

        Parameter promptParameter = new Parameter();
        promptParameter.setPrompt(prompt);
        promptParameter.setMaxTokens(60);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(promptParameter);

        TextCompletionRequest textCompletionRequest = new TextCompletionRequest();
        textCompletionRequest.setModel(model);
        textCompletionRequest.setParameters(parameters);

        CompletionsService completionsService = retrofit.create(CompletionsService.class);

        Call<Completion> completionsCall = completionsService.createCompletion(apiKey, textCompletionRequest);
        Completion completion = completionsCall.execute().body();
        String text = completion.getChoices().get(0).getText();

        return text;
    }
}
