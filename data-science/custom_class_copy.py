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
        idx = np.argmax(probs, axis=1)
        labels = ['A tiempo', 'Retrasado']

        max_prob = np.round(probs[:,1] * 100, 2)

        X_feat = self._transform_until_preprocess(X)

        results = []
        for j in range(len(X)):
            explicacion_texto = self.get_detailed_explanation(X.iloc[[j]])

            results.append({
                "Predicción": str(labels[idx[j]]),
                "Probabilidad de retraso": float(max_prob[j]),
                "Distancia": float(X_feat.iloc[j]['distancia']),
                "Explicabilidad": str(explicacion_texto)
            })
        return results

    def get_detailed_explanation(self, X_single):
        X_feat = self._transform_until_preprocess(X_single)
        X_trans = self.prep.transform(X_feat)

        shap_values = self.explainer.shap_values(X_trans)
        if isinstance(shap_values, list):
            shap_values = shap_values[1]

        shap_df = pd.DataFrame(shap_values, columns=self.feature_names)

        feature_groups = {
            "hora de vuelo": [c for c in shap_df.columns if "hora_" in c],
            "mes de vuelo": [c for c in shap_df.columns if "mes_" in c],
            "día de la semana": [c for c in shap_df.columns if "dia_semana_" in c],
        }

        grouped_values = shap_df.copy()
        for group, cols in feature_groups.items():
            if cols:
                grouped_values[group] = shap_df[cols].sum(axis=1)
                grouped_values.drop(columns=cols, inplace=True)

        grouped_values = grouped_values.rename(columns=lambda c: c.split('__', 1)[-1])

        row_values = grouped_values.iloc[0]
        abs_values = row_values.abs()
        total_influence = abs_values.sum()

        if total_influence == 0:
            return "No hay factores significativos detectados para esta predicción."

        sorted_indices = abs_values.sort_values(ascending=False).index

        top_3 = sorted_indices[:3]
        narrativa_partes = []

        for feature in top_3:
            val = row_values[feature]
            porcentaje = float((abs(val) / total_influence) * 100)
            tendencia = "aumentar" if val > 0 else "reducir"
            narrativa_partes.append(f"{feature} ({porcentaje:.2f}% de influencia con tendencia a {tendencia} la estimación de retraso)")

        restantes_num = len(sorted_indices) - 3

        restantes_infl = float((abs_values[sorted_indices[3:]].sum() / total_influence) * 100) if restantes_num > 0 else 0.0

        final_text = (
            f"Para esta predicción, el modelo tomó su decisión considerando principalmente: \n "
            f"{narrativa_partes[0]}, \n "
            f"{narrativa_partes[1]}, \n "
            f"y {narrativa_partes[2]}.\n"
            f"Las características restantes ({restantes_num}) agrupan el resto de las influencias ({restantes_infl:.2f}%)."
        )

        return final_text

    def explain(self, X):
        X_feat = self._transform_until_preprocess(X)
        X_trans = self.prep.transform(X_feat)
        shap_values = self.explainer.shap_values(X_trans)
        if isinstance(shap_values, list): shap_values = shap_values[1]

        shap_df = pd.DataFrame(shap_values, columns=self.feature_names)
        grouped_shap = shap_df.abs().T
        grouped_shap.columns = ["Importancia"]
        return grouped_shap.sort_values(by='Importancia', ascending=False)