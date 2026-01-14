import shap
import pandas as pd
import numpy as np

class CustomPrediction:
    def __init__(self, pipeline):
        self.pipeline = pipeline

        self.gen_date = pipeline.named_steps["gen-date"]
        self.gen_dist = pipeline.named_steps["gen-dist"]
        self.prep = pipeline.named_steps["preprocessing"]
        self.model = pipeline.named_steps["model"]

        self.feature_names = self.prep.get_feature_names_out()

        self.explainer = shap.TreeExplainer(self.model)

    def _transform_until_preprocess(self, X):
        X = self.gen_date.transform(X)
        X = self.gen_dist.transform(X)
        return X

    def predict(self, X):
        probs = self.pipeline.predict_proba(X)
        idx  = np.argmax(probs, axis=1)
        labels = ['A tiempo', 'Retrasado']
        # max_prob = np.round(np.max(probs, axis=1), 2)
        max_prob = np.round(probs[:,1] * 100, 2)
        max_prob = max_prob.astype(float)

        X_feat = self._transform_until_preprocess(X)

        return [{"Predicción": labels[i], "Probabilidad de retraso": p, "Distancia":  X_feat.iloc[j]['distancia']} for j, (i, p) in enumerate(zip(idx, max_prob))]

    def explain(self, X):

        X_feat = self._transform_until_preprocess(X)
        X_trans = self.prep.transform(X_feat)

        shap_values = self.explainer.shap_values(X_trans)

        shap_df = pd.DataFrame(
                              shap_values,
                              columns=self.feature_names
                          )
        

        feature_groups = {
                        "hora de vuelo": [c for c in shap_df.columns if "hora_" in c],
                        "mes de vuelo": [c for c in shap_df.columns if "mes_" in c],
                        "día de la semana": [c for c in shap_df.columns if "dia_semana_" in c],
                    }

        grouped_shap = shap_df.copy()

        for group, cols in feature_groups.items():
            grouped_shap[group] = shap_df[cols].sum(axis=1)
            grouped_shap.drop(columns=cols, inplace=True)
            
        grouped_shap = grouped_shap.rename(columns=lambda c: c.split('__', 1)[-1])

        grouped_shap = grouped_shap.abs().T
        grouped_shap.columns = ["Importancia"]
        grouped_shap = grouped_shap.sort_values(by='Importancia', ascending=False)

        return grouped_shap

