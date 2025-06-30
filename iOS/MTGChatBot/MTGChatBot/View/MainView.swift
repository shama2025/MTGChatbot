//
//  MainView.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/19/25.
//  This is the main/home view of the application where users can input queries and get responses back from AI

import Foundation
import SwiftUI

struct MainView: View {
    @State private var isPresented = false
    @State private var inputText = ""
    @FocusState private var isFocused: Bool

    @StateObject private var chatsVM = ChatModel()

    private let cardViewController = CardViewController()
    private let cardRulesViewController = CardRulesViewController()
    private let cardSetViewController = CardSetViewController()
    private let cardDataFormatter = CardDataFormatter()

    var body: some View {
        ZStack {
            LinearGradient(
                gradient: Gradient(colors: [.white, .blue, .black, .red, .green]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .opacity(0.25)
            .ignoresSafeArea()

            VStack {
                ChatBoxView(chats: chatsVM.chats)
                    .animation(.default, value: chatsVM.chats.count)
                Spacer()
                inputArea
            }
            .padding()
        }
    }

    private var inputArea: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)

            TextField("Card Name Rhystic Study", text: $inputText)
                .padding(10)
                .background(Color(.systemGray6))
                .cornerRadius(10)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.black.opacity(0.5), lineWidth: 1)
                )
                .font(.body)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .focused($isFocused)
                .onSubmit {
                    Task {
                        await handleInput(inputText)
                    }
                }
                .onAppear {
                    isFocused = true
                }

            Button {
                isPresented.toggle()
            } label: {
                Image(systemName: "key.fill")
            }
            .sheet(isPresented: $isPresented) {
                PhraseKeyBottomSheet().presentationDetents([.fraction(0.30)])
            }
            .foregroundColor(.black)
        }
    }

    @MainActor
    private func handleInput(_ input: String) async {
        defer { inputText = "" } // Clear input after handling

        guard !input.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }

        do {
            if let genCard = try? /card name (.+)/.ignoresCase().firstMatch(in: input) {
                let cardName = genCard.1
                print(cardName)
                let data = try await cardViewController.getGenCardData(cardName: String(cardName))
                let aiChat = cardDataFormatter.formatCardData(cardData: data)
                chatsVM.chats.append(Chat(user: input, ai: aiChat))
                return
            }

            if let cardRules = try? /card rules (.+)/.ignoresCase().firstMatch(in: input) {
                let cardName = cardRules.1
                _ = try await cardViewController.getGenCardData(cardName: String(cardName))
                try await Task.sleep(nanoseconds: 500_000)
                guard let rulingsID = CacheManager.shared.object(forKey: "rulingsID" as NSString) else { return }

                let data = try await cardRulesViewController.getCardRulesData(cardRuleID: rulingsID as String)

                let aiChat = cardDataFormatter.handleCardRulesData(rulesData: data)
                chatsVM.chats.append(Chat(user: input, ai: aiChat))
                print(chatsVM.chats)

                return
            }

            if let cardSet = try? /card set (.+)/.ignoresCase().firstMatch(in: input) {
                let cardName = cardSet.1
                _ = try await cardViewController.getGenCardData(cardName: String(cardName))
                try await Task.sleep(nanoseconds: 500_000)
                guard let setID = CacheManager.shared.object(forKey: "setID" as NSString) else { return }
                let data = try await cardSetViewController.getCardSetData(cardSetID: setID as String)
                let aiChat = cardDataFormatter.handleCardSetData(setData: data)
                chatsVM.chats.append(Chat(user: input, ai: aiChat))
                print(chatsVM.chats)

                return
            }

        } catch {
            chatsVM.chats.append(Chat(user: input, ai: "Oops! Something went wrong: \(error.localizedDescription)"))
        }
    }
}

#Preview {
    MainView()
}
