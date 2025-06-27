//
//  PhraseKeyBottomSheet.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//  Bottom sheet with the purpose of supplying key for phrases

import SwiftUI

struct PhraseKeyBottomSheet: View {
    @Environment(\.dismiss) var dismiss

    var body: some View {
        ZStack {
            LinearGradient(
                gradient: Gradient(colors: [.white, .blue, .black, .red, .green]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .opacity(0.25)
            .ignoresSafeArea()

            VStack(alignment: .leading, spacing: 16) {
                // Dismiss button
                HStack {
                    Spacer()
                    Button(action: {
                        dismiss()
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundColor(.gray)
                    }
                }

                // Title
                Text("Phrase Key")
                    .font(.title)
                    .fontWeight(.bold)
                    .padding(.bottom, 8)

                // Help items
                Group {
                    PhraseRow(command: "card name ___", description: "Displays the name and general info of the card")
                    PhraseRow(command: "card rules ___", description: "Displays specific rulings of the card")
                    PhraseRow(command: "card set ___", description: "Displays set information of the card")
                }

                Spacer()
            }
            .padding()
        }
    }
}

#Preview {
    PhraseKeyBottomSheet()
}
